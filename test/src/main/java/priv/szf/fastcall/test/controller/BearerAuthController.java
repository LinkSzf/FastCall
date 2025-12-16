package priv.szf.fastcall.test.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.test.pojo.BearerAuthRequestBody;
import priv.szf.fastcall.test.pojo.BearerAuthResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth/bearer")
public class BearerAuthController {

    private static final String TOKEN = "bearer_token_1234567890";

    private static final long EXPIRE = 60;

    private LocalDateTime lastAuthTime = LocalDateTime.now().minusSeconds(EXPIRE);


    @PostMapping
    public BearerAuthResponseBody bearerAuth(@RequestBody BearerAuthRequestBody bearerAuthRequestBody) {
        System.out.printf(
                "BearerAuthRequestBody: user=%s, pwd=%s%n",
                bearerAuthRequestBody.getUser(),
                bearerAuthRequestBody.getPwd()
        );

        // 通过这个检测在客户端持有的token失效且并发访问的情况下，客户端能否有效进行并发控制
        if (LocalDateTime.now().minusSeconds(EXPIRE).isBefore(lastAuthTime)) {
            System.out.println("请勿短期重复申请！");
            throw new RuntimeException(
                    String.format("请勿短期重复申请！上次申请时间为：%s, 冷却期为：%s秒", lastAuthTime, EXPIRE)
            );
        }

        this.lastAuthTime = LocalDateTime.now();

        return BearerAuthResponseBody.builder()
                .timestamp(System.currentTimeMillis())
                .system(new BearerAuthResponseBody.System(TOKEN, EXPIRE))
                .build();
    }


    @GetMapping("/resource")
    public ResponseEntity<String> getResource(HttpServletRequest  request) {
        System.out.println("资源被请求了！");
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.endsWith(authorization, TOKEN)) {
            return ResponseEntity.ok().body("拒绝访问资源：未认证！");
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .body("Hello World!");
    }


}
