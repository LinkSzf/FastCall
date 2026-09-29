package io.github.linkszf.fastcall.test.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(ApiKeyAuthController.BASE_URI)
public class ApiKeyAuthController extends BaseAuthController {

    protected static final String BASE_URI = "/auth/api-key";

    private final String key = "link";

    private final String value = "123456";

    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "apikey-system";
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        String headerApiKey = request.getHeader(key);
        String paramApiKey = request.getParameter(key);
        String apikey = (headerApiKey == null) ? paramApiKey : headerApiKey;

        return value.equals(apikey);
    }

}
