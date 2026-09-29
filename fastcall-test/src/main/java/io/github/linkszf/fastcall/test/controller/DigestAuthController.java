package io.github.linkszf.fastcall.test.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(DigestAuthController.BASE_URI)
public class DigestAuthController extends BaseAuthController {

    protected static final String BASE_URI = "/auth/digest";

    // 预定义的合法用户凭据
    private static final String VALID_USERNAME = "link";
    private static final String VALID_PASSWORD = "123456";
    private static final String REALM = "Test Realm";
    private static final String NONCE = "dcd98b7102dd2f0e8b11d0f600bfb0c093";
    private static final String OPAQUE = "5ccc069c403ebaf9f0171e9517f40e41";


    @Override
    protected String getBaseUri() {
        return BASE_URI;
    }

    @Override
    protected String getSystemCode() {
        return "digest-system";
    }

    @Override
    protected ResponseEntity<?> unauthorizedResponse() {
        String authHeader = String.format(
                "Digest realm=\"%s\", qop=\"auth\", nonce=\"%s\", opaque=\"%s\", algorithm=MD5",
                REALM, NONCE, OPAQUE
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", authHeader)
                .body("Failure!请先获得认证！");
    }

    @Override
    protected boolean checkAuth(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            return false;
        }

        // 解析Authorization头
        Map<String, String> authParams = parseAuthorizationHeader(authorization.substring(7));

        // 验证请求参数是否完整
        if (!authParams.containsKey("username") || !authParams.containsKey("realm") ||
                !authParams.containsKey("nonce") || !authParams.containsKey("uri") ||
                !authParams.containsKey("response") || !authParams.containsKey("qop") ||
                !authParams.containsKey("nc") || !authParams.containsKey("cnonce")) {
            return false;
        }

        // 验证用户名
        if (!VALID_USERNAME.equals(authParams.get("username"))) {
            return false;
        }

        // 验证realm和nonce
        if (!REALM.equals(authParams.get("realm")) || !NONCE.equals(authParams.get("nonce"))) {
            return false;
        }

        // 验证opaque
        if (!OPAQUE.equals(authParams.get("opaque"))) {
            return false;
        }

        // 计算正确的response并验证
        String validResponse = calculateDigestResponse(
                authParams.get("username"),
                authParams.get("realm"),
                VALID_PASSWORD,
                authParams.get("nonce"),
                authParams.get("nc"),
                authParams.get("cnonce"),
                authParams.get("qop"),
                request.getMethod(),
                authParams.get("uri")
        );

        if (!validResponse.equals(authParams.get("response"))) {
            return false;
        }

        return true;
    }

    // 解析Authorization头为键值对
    private Map<String, String> parseAuthorizationHeader(String header) {
        Map<String, String> params = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)=[\"]?([^\",]+)[\"]?");
        Matcher matcher = pattern.matcher(header);

        while (matcher.find()) {
            params.put(matcher.group(1), matcher.group(2));
        }

        return params;
    }

    // 计算Digest认证的response值
    private String calculateDigestResponse(String username, String realm, String password,
                                           String nonce, String nc, String cnonce,
                                           String qop, String method, String uri) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算HA1 = MD5(username:realm:password)
            String ha1 = username + ":" + realm + ":" + password;
            String ha1Hex = toHexString(md.digest(ha1.getBytes(StandardCharsets.UTF_8)));

            // 计算HA2 = MD5(method:uri)
            String ha2 = method + ":" + uri;
            String ha2Hex = toHexString(md.digest(ha2.getBytes(StandardCharsets.UTF_8)));

            // 计算response = MD5(HA1:nonce:nc:cnonce:qop:HA2)
            String response = ha1Hex + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2Hex;
            return toHexString(md.digest(response.getBytes(StandardCharsets.UTF_8)));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("algorithm not available", e);
        }
    }

    // 字节数组转16进制字符串
    private String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
