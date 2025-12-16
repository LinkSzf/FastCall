package priv.szf.fastcall.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth/basic")
public class BasicAuthController {

    @GetMapping
    public String basicAuth(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        System.out.println("Test-BasicAuth内容:" + authorization);
        return authorization;
    }










}
