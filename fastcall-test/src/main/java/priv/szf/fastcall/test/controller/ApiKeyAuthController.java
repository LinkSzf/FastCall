package priv.szf.fastcall.test.controller;


import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(ApiKeyAuthController.BASE_URI)
public class ApiKeyAuthController {

    private static final String SYSTEM_CODE = "apikey-system";

    protected static final String BASE_URI = "/auth/api-key";

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
    public ResponseEntity<String> resource(HttpServletRequest request) {
        String headerApiKey = request.getHeader("pwd");
        String paramApiKey = request.getParameter("pwd");
        String apikey = (headerApiKey == null) ? paramApiKey : headerApiKey;
        String position = (headerApiKey == null) ? "param" : "header";

        System.out.println(String.format("Test-ApiKeyAuth:请求到资源。auth内容：%s, 位置：%s", apikey, position));

        return ResponseEntity.ok()
                .body(String.format(
                        "Succeed!获取到资源，使用的ApiKey:[%s], 随机数:[%s]",
                        apikey,
                        RandomUtil.randomChinese()
                ));
    }

}
