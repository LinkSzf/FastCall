package priv.szf.fastcall.test.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;


@RestController
@RequestMapping(BasicAuthController.BASE_URI)
public class BasicAuthController extends BaseAuthController {

    protected static final String BASE_URI = "/auth/basic";

    private final String username = "link";

    private final String password = "123456";

    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "basic-system";
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String credential = StrUtil.removePrefix(authorization, "Basic ");
        String correctCredential = String.join(":", username, password);
        String encodedCredential = Base64.getEncoder().encodeToString(correctCredential.getBytes());
        return encodedCredential.equals(credential);
    }


}
