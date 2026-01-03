package priv.szf.fastcall.test.controller;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(NoneAuthController.BASE_URI)
public class NoneAuthController {

    private static final String SYSTEM_CODE = "none-system";

    protected static final String BASE_URI = "/auth/none";

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

    @GetMapping(RESOURCE_URI)
    public String resource(HttpServletRequest request) {
        return "Hello World!" + RandomUtil.randomChinese();
    }
}
