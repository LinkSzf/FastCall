package priv.szf.fastcall.test.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.test.model.CookieRequestBody;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(CookieAuthController.BASE_URI)
public class CookieAuthController {

    private static final String COOKIE = "SESSION_ID=123456";

    private static final String USERNAME = "link";

    private static final String PASSWORD = "123456";

    private static final String SYSTEM_CODE = "cookie-system";

    protected static final String BASE_URI = "/auth/cookie";

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
    public ResponseEntity<String> login(@RequestBody CookieRequestBody loginBody) {
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        if (!StringUtils.equals(username, USERNAME) || !StringUtils.equals(password, PASSWORD)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("认证失败！");
        }

        return ResponseEntity.ok()
                .header("Set-Cookie", COOKIE)
                .body("认证成功！");
    }


    @GetMapping(RESOURCE_URI)
    public ResponseEntity<String> resource(HttpServletRequest request) {
        System.out.println("Test-CookieAuth:请求到资源。");
        String cookie = request.getHeader("Cookie");
        if (!StrUtil.equals(cookie, COOKIE)) {
            System.out.println("Test-CookieAuth:拒绝访问资源：未认证！");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("拒绝访问资源：未认证！");
        }
        return ResponseEntity.ok()
                .body(String.format(
                        "Succeed!获取到资源，使用的CookieAuth:[%s], 随机数:[%s]",
                        cookie,
                        RandomUtil.randomChinese()
                ));
    }



}
