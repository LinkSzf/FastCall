package io.github.linkszf.fastcall.test.controller;

import cn.hutool.core.util.RandomUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import io.github.linkszf.fastcall.core.FastCall;
import io.github.linkszf.fastcall.core.FastCallResponse;
import io.github.linkszf.fastcall.test.model.TestResponse;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public abstract class BaseAuthController {

    protected static final String TEST_URI = "/test";

    protected static final String RESOURCE_URI = "/resource";

    protected abstract String getBaseUri();

    protected abstract String getSystemCode();

    protected abstract boolean checkAuth(HttpServletRequest request);

    @Getter
    @Autowired
    private FastCall fastCall;

    @RequestMapping(TEST_URI)
    protected TestResponse test() {
        String uri = getBaseUri() + RESOURCE_URI;
        FastCallResponse<Object> response = fastCall.getClient(getSystemCode())
                .newCall()
                .uri(uri)
                .prepared()
                .callIt();
        return new TestResponse(response);
    }

    @GetMapping(RESOURCE_URI)
    public ResponseEntity<?> resource(HttpServletRequest request) {
        log.info("[TEST-({})-资源]正在被请求...", getSystemCode());
        if (!checkAuth(request)) {
            log.info("[TEST-({})-资源]请求未获得认证或认证失效！", getSystemCode());
            return unauthorizedResponse();
        }
        log.info("[TEST-({})-资源]资源被成功获取", getSystemCode());
        return buildResource();
    }

    protected ResponseEntity<?> buildResource() {
        return ResponseEntity.ok()
                .body(String.format(
                        "Succeed!获取到资源:[%s]",
                        RandomUtil.randomChinese()
                ));
    }

    protected ResponseEntity<?> unauthorizedResponse() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Failure!请先获得认证！");
    }
}
