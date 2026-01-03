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
@RequestMapping(BasicAuthController.BASE_URI)
public class BasicAuthController {

    private static final String SYSTEM_CODE = "basic-system";

    protected static final String BASE_URI = "/auth/basic";

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
        String authorization = request.getHeader("Authorization");

        System.out.println("Test-ApiKeyAuth:请求到资源。auth内容：" + authorization);

        return ResponseEntity.ok()
                .body(String.format(
                        "Succeed!获取到资源，使用的BasicAuth:[%s], 随机数:[%s]",
                        authorization,
                        RandomUtil.randomChinese()
                ));
    }










}
