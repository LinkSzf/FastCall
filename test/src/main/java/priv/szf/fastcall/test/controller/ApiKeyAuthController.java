package priv.szf.fastcall.test.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth/api-key")
public class ApiKeyAuthController {

    @GetMapping
    public String apiKeyAuth(HttpServletRequest request) {
        String headerApiKey = request.getHeader("pwd");
        String paramApiKey = request.getParameter("pwd");
        String apikey = (headerApiKey == null) ? paramApiKey : headerApiKey;
        String position = (headerApiKey == null) ? "param" : "header";
        System.out.println(String.format("Test-ApiKey内容：%s, 位置：%s", apikey, position));
        return apikey;
    }

}
