package io.github.linkszf.fastcall.test.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(JwtAuthController.BASE_URI)
public class JwtAuthController extends BaseAuthController {

    protected static final String BASE_URI = "/auth/jwt";

    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "jwt-system";
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        return true;
    }

    @GetMapping("/weather")
    public Map<String, Object> weather() {
        String system = "weather-system";
        return getFastCall().getClient(system)
                .<Map<String, Object>>newCall(Map.class)
                .uri("/v7/weather/now?location=116.41,39.92")
                .prepared()
                .call();
    }

}
