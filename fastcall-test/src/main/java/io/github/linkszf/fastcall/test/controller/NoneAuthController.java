package io.github.linkszf.fastcall.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(NoneAuthController.BASE_URI)
public class NoneAuthController extends BaseAuthController {

    protected static final String BASE_URI = "/auth/none";

    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "noauth-system";
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        return true;
    }


}
