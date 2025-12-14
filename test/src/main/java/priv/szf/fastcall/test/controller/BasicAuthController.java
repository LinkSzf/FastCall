package priv.szf.fastcall.test.controller;

import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class BasicAuthController {

    @GetMapping("/basic")
    public String basicAuth(HttpRequest request) {
        List<String> authorization = request.getHeaders().get("Authorization");
        System.out.println(authorization);
        return "hello world!";
    }





}
