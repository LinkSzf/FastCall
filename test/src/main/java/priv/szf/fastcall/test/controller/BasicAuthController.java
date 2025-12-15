package priv.szf.fastcall.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class BasicAuthController {

    @GetMapping("/basic")
    public String basicAuth(HttpServletRequest request) {
        String authorization = request.getHeader("pwd");
        System.out.println(authorization);
        return "hello world!";
    }





}
