package priv.szf.fastcall.test.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.test.pojo.BearerAuthRequestBody;
import priv.szf.fastcall.test.pojo.BearerAuthResponseBody;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth/bearer")
public class BearerAuthController {

    private static final String TOKEN = "bearer_token_1234567890";

    private static final long EXPIRE = 60;


    @PostMapping
    public BearerAuthResponseBody bearerAuth(@RequestBody BearerAuthRequestBody bearerAuthRequestBody) {
        System.out.printf(
                "BearerAuthRequestBody: user=%s, pwd=%s%n",
                bearerAuthRequestBody.getUser(),
                bearerAuthRequestBody.getPwd()
        );

        return BearerAuthResponseBody.builder()
                .timestamp(System.currentTimeMillis())
                .system(new BearerAuthResponseBody.System(TOKEN, EXPIRE))
                .build();
    }


    @GetMapping("/resource")
    public String getResource(HttpServletRequest  request) {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.endsWith(authorization, TOKEN)) {
            return "拒绝访问资源：未认证！";
        }
        return "Hello World!";
    }


}
