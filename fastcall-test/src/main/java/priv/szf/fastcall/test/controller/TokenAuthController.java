package priv.szf.fastcall.test.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.test.model.BearerAuthRequestBody;
import priv.szf.fastcall.test.model.BearerAuthResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(TokenAuthController.BASE_URI)
public class TokenAuthController extends BaseAuthController {

    private static final String TOKEN = "Bearer 123456";

    private static final long EXPIRE = 3;

    private static final String USERNAME = "link";

    private static final String PASSWORD = "123456";

    protected static final String BASE_URI = "/auth/bearer";

    private LocalDateTime authExpireTime;


    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "bearer-system";
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        if (isTokenExpired()) {
            return false;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return TOKEN.equals(authorization);
    }

    @PostMapping("/login")
    public BearerAuthResponseBody login(@RequestBody BearerAuthRequestBody requestBody) {
        log.info("[TEST-({})-登录]正在被请求...", getSystemCode());

        checkConRequest();

        String usr = requestBody.getUser();
        String pwd = requestBody.getPwd();

        if (!USERNAME.equals(usr) || !PASSWORD.equals(pwd)) {
            throw new RuntimeException(String.format(
                    "Test-TokenAuth: 用户名或密码错误！期望值:[%s], 实际值:[%s]",
                    USERNAME + "|" + PASSWORD,
                    usr + "|" + pwd
            ));
        }

        log.info("[TEST-({})-登录]用户校验通过！", getSystemCode());
        this.authExpireTime = LocalDateTime.now().plusSeconds(EXPIRE);

        return BearerAuthResponseBody.builder()
                .timestamp(System.currentTimeMillis())
                .system(new BearerAuthResponseBody.System(TOKEN, EXPIRE+20))
                .build();
    }

    /**
     * 检查并发请求，防止短时间内重复申请token
     * <p>
     * 该方法用于模拟客户端token失效时并发访问的情况，检测客户端是否能有效进行并发控制。
     * 通过记录上次认证时间，确保两次认证请求之间有足够的冷却时间(EXPIRE秒)。
     * </p>
     *
     * @throws RuntimeException 如果检测到短期内重复访问，则抛出异常
     */
    private void checkConRequest() {
        if (!isTokenExpired() && authExpireTime != null) {
            log.info("[TEST-({})-登录]短期内重复访问！", getSystemCode());
            throw new RuntimeException(
                    String.format("Test-TokenAuth: 请勿短期重复申请！上次申请时间为：%s, 冷却期为：%s秒", authExpireTime, EXPIRE)
            );
        }
    }

    private boolean isTokenExpired() {
        return authExpireTime != null && LocalDateTime.now().isAfter(authExpireTime);
    }


}
