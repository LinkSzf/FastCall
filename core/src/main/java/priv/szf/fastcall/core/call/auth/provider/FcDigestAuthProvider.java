package priv.szf.fastcall.core.call.auth.provider;

import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.DigestAuth;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FcDigestAuthProvider extends FcDefaultFcAuthProvider<DigestAuth> {
    private static final AtomicInteger NONCE_COUNT = new AtomicInteger(0);

    public FcDigestAuthProvider(String identity) {
        super(identity);
    }

    @Override
    public FcTokenPak mapToToken(Request request, FastCallResponse<String> response, DigestAuth authContent) {
        super.checkSuccess(response);

        String authenticateHeader = response.getHeader(FcHttpHeader.WWW_AUTHENTICATE.getName());
        if (StringUtils.startsWith(authenticateHeader, "Digest")) {
            throw new FcUnexpectedException(
                    String.format("FastCall-系统[%s]刷新认证失败，未识别到Digest认证头，code[%s], message[%s], data[%s]",
                            getIdentity(), response.getCode(), response.getMessage(), response.getData())
            );
        }

        Map<String, String> challengeParams = parseChallenge(authenticateHeader);
        String authHeader = createAuthHeader(request, authContent, challengeParams);



        return FcTokenPak.builder()
                .token(tokenStr)
                .issuance(issuance)
                .estimatedExpiration(estimatedExpiration)
                .build();
    }

    /**
     * 解析服务器返回的挑战字符串
     * 示例：Digest realm="testrealm@host.com", nonce="dcd98b7102dd2f0e8b11d0f600bfb0c093"
     */
    private Map<String, String> parseChallenge(String authenticateHeader) {
        Map<String, String> params = new HashMap<>();

        // 移除"Digest "前缀
        String challenge = StringUtils.removeStart(authenticateHeader, AuthType.DIGEST.getPrefix());

        // 分割参数
        String[] parts = challenge.split(",");

        for (String part : parts) {
            part = part.trim();
            int equalsIndex = part.indexOf('=');

            if (equalsIndex > 0) {
                String key = part.substring(0, equalsIndex).trim();
                String value = part.substring(equalsIndex + 1).trim();

                // 移除可能存在的引号
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                params.put(key, value);
            }
        }

        return params;
    }

    /**
     * 创建Authorization头
     */
    private String createAuthHeader(Request request, DigestAuth authContent, Map<String, String> challengeParams) {
        String realm = challengeParams.get("realm");
        String nonce = challengeParams.get("nonce");
        String qop = challengeParams.get("qop");
        String opaque = challengeParams.get("opaque");
        String algorithm = challengeParams.getOrDefault("algorithm", "MD5");

        // 获取请求方法和URI
        String method = request.method();
        String uri = request.url().encodedPath();

        // 生成客户端nonce
        String cnonce = generateClientNonce();

        // 计算nc值（nonce计数器）
        String nc = String.format("%08x", NONCE_COUNT.incrementAndGet());

        String username = authContent.getUsername();
        String password = authContent.getPassword();

        try {
            // 计算HA1 = MD5(username:realm:password)
            String ha1 = md5(username + ":" + realm + ":" + password);

            // 如果算法是MD5-sess，需要额外的计算
            if ("MD5-sess".equalsIgnoreCase(algorithm)) {
                ha1 = md5(ha1 + ":" + nonce + ":" + cnonce);
            }

            // 计算HA2 = MD5(method:uri)
            String ha2 = md5(method + ":" + uri);

            // 计算response
            String response;
            if (qop != null && (qop.equals("auth") || qop.equals("auth-int"))) {
                // 当有qop时：response = MD5(HA1:nonce:nc:cnonce:qop:HA2)
                response = md5(ha1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);
            } else {
                // 当没有qop时（旧版本）：response = MD5(HA1:nonce:HA2)
                response = md5(ha1 + ":" + nonce + ":" + ha2);
            }

            // 构建Authorization头
            StringBuilder authHeader = new StringBuilder("Digest ");
            authHeader.append("username=\"").append(username).append("\", ");
            authHeader.append("realm=\"").append(realm).append("\", ");
            authHeader.append("nonce=\"").append(nonce).append("\", ");
            authHeader.append("uri=\"").append(uri).append("\", ");
            authHeader.append("response=\"").append(response).append("\"");

            if (qop != null) {
                authHeader.append(", qop=").append(qop);
                authHeader.append(", nc=").append(nc);
                authHeader.append(", cnonce=\"").append(cnonce).append("\"");
            }

            if (algorithm != null) {
                authHeader.append(", algorithm=").append(algorithm);
            }

            if (opaque != null) {
                authHeader.append(", opaque=\"").append(opaque).append("\"");
            }

            return authHeader.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 生成客户端nonce（随机字符串）
     */
    private String generateClientNonce() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    /**
     * MD5哈希计算
     */
    private String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        byte[] digest = md.digest();

        // 转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
