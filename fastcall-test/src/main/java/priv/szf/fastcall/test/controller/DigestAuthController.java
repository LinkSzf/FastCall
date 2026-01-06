package priv.szf.fastcall.test.controller;


import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;

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
public class DigestAuthController {

    private static final String SYSTEM_CODE = "digest-system";

    protected static final String BASE_URI = "/auth/digest";

    private static final String RESOURCE_URI = "/resource";

    // 预定义的合法用户凭据
    private static final String VALID_USERNAME = "link";
    private static final String VALID_PASSWORD = "123456";
    private static final String REALM = "Test Realm";
    private static final String NONCE = "dcd98b7102dd2f0e8b11d0f600bfb0c093";
    private static final String OPAQUE = "5ccc069c403ebaf9f0171e9517f40e41";

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

        if (authorization == null || !authorization.startsWith("Digest ")) {
            String authHeader = String.format(
                    "Digest realm=\"%s\", qop=\"auth\", nonce=\"%s\", opaque=\"%s\", algorithm=MD5",
                    REALM, NONCE, OPAQUE
            );
            System.out.println("Test-DigestAuth: 尚未认证，先返回认证凭证");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", authHeader)
                    .body("资源未认证，请先行认证!");
        }

        System.out.println("Test-DigestAuth: 已携带认证信息，开始核对凭证");

        // 解析Authorization头
        Map<String, String> authParams = parseAuthorizationHeader(authorization.substring(7));

        // 验证请求参数是否完整
        if (!authParams.containsKey("username") || !authParams.containsKey("realm") ||
                !authParams.containsKey("nonce") || !authParams.containsKey("uri") ||
                !authParams.containsKey("response") || !authParams.containsKey("qop") ||
                !authParams.containsKey("nc") || !authParams.containsKey("cnonce")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("认证相关参数不完整！");
        }

        // 验证用户名
        if (!VALID_USERNAME.equals(authParams.get("username"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名不正确！！");
        }

        // 验证realm和nonce
        if (!REALM.equals(authParams.get("realm")) || !NONCE.equals(authParams.get("nonce"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("realm或nonce不正确");
        }

        // 验证opaque
        if (!OPAQUE.equals(authParams.get("opaque"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("opaque不正确");
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("response整体校验失败！");
        }

        System.out.println("Test-DigestAuth: 认证通过。成功请求到资源");

        return ResponseEntity.ok()
                .body(String.format(
                        "Succeed!获取到资源，使用的DigestAuth:[%s], 随机数:[%s]",
                        authorization,
                        RandomUtil.randomChinese()
                ));
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
