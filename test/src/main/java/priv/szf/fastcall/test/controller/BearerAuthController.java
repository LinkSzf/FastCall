package priv.szf.fastcall.test.controller;


import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.test.pojo.BearerAuthRequestBody;
import priv.szf.fastcall.test.pojo.BearerAuthResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(BearerAuthController.BASE_URI)
public class BearerAuthController {

    private static final String TOKEN = "bearer_token_1234567890";

    private static final long EXPIRE = 3;

    private static final String USERNAME = "link";

    private static final String PASSWORD = "123456";

    private LocalDateTime lastAuthTime = LocalDateTime.now().minusSeconds(EXPIRE);

    private static final String SYSTEM_CODE = "bearer-system";

    protected static final String BASE_URI = "/auth/bearer";

    private static final String RESOURCE_URI = "/resource";

    @Autowired
    private FastCall fastCall;

    @GetMapping("/test")
    public Object testAuth() {
        return fastCall.getClient(SYSTEM_CODE)
                .newCall()
                .uri(BASE_URI + RESOURCE_URI)
                .prepared()
                .callIt();
    }


    @PostMapping("/login")
    public BearerAuthResponseBody login(@RequestBody BearerAuthRequestBody requestBody) {
        // 校验用户名密码
        String usr = requestBody.getUser();
        String pwd = requestBody.getPwd();

        if (!USERNAME.equals(usr) || !PASSWORD.equals(pwd)) {
            throw new RuntimeException(String.format(
                    "Test-BearerTokenAuth: 用户名或密码错误！期望值:[%s], 实际值:[%s]",
                    USERNAME + "|" + PASSWORD,
                    usr + "|" + pwd
            ));
        }

        System.out.printf(
                "Test-BearerTokenAuth: Token申请-校验通过：user:[%s], pwd[%s]%n",
                usr,
                pwd
        );

        // 通过这个检测在客户端持有的token失效且并发访问的情况下，客户端能否有效进行并发控制
        if (LocalDateTime.now().minusSeconds(EXPIRE).isBefore(lastAuthTime)) {
            System.out.println("Test-BearerTokenAuth: 请勿短期重复申请！");
            throw new RuntimeException(
                    String.format("Test-BearerTokenAuth: 请勿短期重复申请！上次申请时间为：%s, 冷却期为：%s秒", lastAuthTime, EXPIRE)
            );
        }

        this.lastAuthTime = LocalDateTime.now();

        return BearerAuthResponseBody.builder()
                .timestamp(System.currentTimeMillis())
                .system(new BearerAuthResponseBody.System(TOKEN, EXPIRE))
                .build();
    }


    @GetMapping(RESOURCE_URI)
    public ResponseEntity<String> resource(HttpServletRequest  request) {
        System.out.println("Test-BearerTokenAuth:请求到资源。");
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.endsWith(authorization, TOKEN)) {
            System.out.println("Test-BearerTokenAuth:拒绝访问资源：未认证！");
            return ResponseEntity.ok().body("拒绝访问资源：未认证！");
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .body(String.format(
                        "Succeed!获取到资源，使用的BearerAuth:[%s], 随机数:[%s]",
                        authorization,
                        RandomUtil.randomChinese()
                ));
    }


}
