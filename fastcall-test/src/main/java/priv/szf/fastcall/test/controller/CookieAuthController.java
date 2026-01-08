package priv.szf.fastcall.test.controller;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.test.model.CookieRequestBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(CookieAuthController.BASE_URI)
public class CookieAuthController extends BaseAuthController {

    private static final String COOKIE = "SESSION_ID=123456";

    private static final String USERNAME = "link";

    private static final String PASSWORD = "123456";

    protected static final String BASE_URI = "/auth/cookie";

    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "cookie-system";
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        String cookie = request.getHeader(HttpHeaders.COOKIE);
        return COOKIE.equals(cookie);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CookieRequestBody loginBody) {
        log.info("[TEST-{}-登录]正在被请求...", getSystemCode());
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        if (!StrUtil.equals(username, USERNAME) || !StrUtil.equals(password, PASSWORD)) {
            log.info("[TEST-({})-登录]用户校验失败！", getSystemCode());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("认证失败！");
        }

        log.info("[TEST-({})-登录]用户校验通过！", getSystemCode());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, COOKIE)
                .body("认证成功！");
    }



}
