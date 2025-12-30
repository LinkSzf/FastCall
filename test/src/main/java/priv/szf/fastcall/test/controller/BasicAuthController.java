package priv.szf.fastcall.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(BasicAuthController.AUTH_URI)
public class BasicAuthController {

    private static final String SYSTEM_CODE = "basic-system";

    protected static final String AUTH_URI = "/auth/basic";

    private static final String RESOURCE_URI = "/resource";

    @Autowired
    private FastCall fastCall;

    @GetMapping("/test")
    public Object testAuth() {
        return fastCall.getClient(SYSTEM_CODE)
                .newCall()
                .uri(AUTH_URI+RESOURCE_URI)
                .prepared()
                .callIt();
    }

    @GetMapping(RESOURCE_URI)
    public String resource(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        System.out.println("Test-BasicAuth内容:" + authorization);
        return String.format("Succeed!获取到资源，使用的BasicAuth:[%s]", authorization);
    }










}
