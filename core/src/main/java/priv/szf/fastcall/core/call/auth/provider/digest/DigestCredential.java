package priv.szf.fastcall.core.call.auth.provider.digest;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.Digester;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.util.Objects;

public class DigestCredential {

    private String username;

    private String password;

    private String realm;

    private String nonce;

    private String uri;

    private String method;

    private String opaque;

    private String qop = "auth";

    private DigestAlgorithm algorithm = DigestAlgorithm.MD5;

    private String cnonce;

    private String response;

    private int nc;

    private String bodyHash;

    public static Builder builder() {
        return new Builder();
    }

    public String buildCredentialStr() {
        StringBuilder credential = new StringBuilder();

        credential.append("username=\"").append(username).append("\", ");
        credential.append("realm=\"").append(realm).append("\", ");
        credential.append("nonce=\"").append(nonce).append("\", ");
        credential.append("uri=\"").append(uri).append("\", ");

        if (Objects.nonNull(algorithm)) {
            credential.append("algorithm=").append(algorithm).append(", ");
        }

        if (StringUtils.isNotBlank(qop)) {
            String ncString = String.format("%08x", nc);
            credential.append("qop=").append(qop).append(", ");
            credential.append("nc=").append(ncString).append(", ");
            credential.append("cnonce=\"").append(cnonce).append("\", ");
        }

        credential.append("response=\"").append(response).append("\"");

        if (StringUtils.isNotBlank(opaque)) {
            credential.append(", opaque=\"").append(opaque).append("\"");
        }

        return credential.toString();
    }

    private String calculateResponse() {
        String ha1 = calculateHA1();
        String ha2 = calculateHA2();

        String ncString = String.format("%08x", nc);

        String text = (StringUtils.isNotEmpty(qop) ?
                StringUtils.joinWith(":", ha1, nonce, ncString, cnonce, qop, ha2)
                : StringUtils.joinWith(":", ha1, nonce, ha2));

        return hash(text);
    }

    private String calculateHA1() {
        String text = StringUtils.joinWith(":", username, realm, password);
        String ha1 = hash(text);

        if (algorithm.isSessionAlgorithm()) {
            String newText = StringUtils.joinWith(":", ha1, nonce, cnonce);
            ha1 = hash(newText);
        }

        return ha1;
    }

    private String calculateHA2() {
        String text = ("auth-int".equals(qop)) ?
                StringUtils.joinWith(":", method, uri, bodyHash)
                : StringUtils.joinWith(":", method, uri);

        return hash(text);
    }

    private String generateClientNonce() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    private String hash(String text) {
        Digester md = new Digester(algorithm.getHashAlgorithm());

        // 处理SHA-512-256特殊情况（截断的SHA-512）
        if (algorithm == DigestAlgorithm.SHA_512_256) {
            byte[] hash = md.digest(text.getBytes());
            // 截断为256位（32字节）
            byte[] truncated = new byte[32];
            System.arraycopy(hash, 0, truncated, 0, 32);
            return HexUtil.encodeHexStr(truncated);
        }

        return md.digestHex(text.getBytes());
    }

    public static class Builder {

        private final DigestCredential credentials = new DigestCredential();

        public Builder username(String username) {
            this.credentials.username = username;
            return this;
        }

        public Builder password(String password) {
            this.credentials.password = password;
            return this;
        }

        public Builder challenge(DigestChallenge challenge) {
            this.credentials.realm = challenge.getRealm();
            this.credentials.nonce = challenge.getNonce();
            this.credentials.opaque = challenge.getOpaque();

            if (StringUtils.isNotEmpty(challenge.getQop())) {
                this.credentials.qop = challenge.getQop();
            }

            if (StringUtils.isNotEmpty(challenge.getAlgorithm())) {
                this.credentials.algorithm = DigestAlgorithm.fromString(challenge.getAlgorithm());
            }

            return this;
        }

        public Builder uri(String uri) {
            this.credentials.uri = uri;
            return this;
        }

        public Builder method(String method) {
            this.credentials.method = method;
            return this;
        }

        public Builder nc(int nc) {
            this.credentials.nc = nc;
            return this;
        }

        public Builder body(byte[] body) {
            if (Objects.isNull(body)) {
                body = new byte[0];
            }
            this.credentials.bodyHash = credentials.hash(new String(body));
            return this;
        }

        public DigestCredential build() {
            if (StringUtils.isAnyEmpty(credentials.username, credentials.password)) {
                throw new IllegalArgumentException("用户名和密码不能为空");
            }

            if (StringUtils.isAnyEmpty(credentials.realm, credentials.nonce)) {
                throw new IllegalArgumentException("realm和nonce不能为空");
            }

            if (StringUtils.isAnyEmpty(credentials.uri, credentials.method)) {
                throw new IllegalArgumentException("URI和HTTP方法不能为空");
            }

            if ("auth-int".equals(credentials.qop) && StringUtils.isEmpty(credentials.bodyHash)) {
                throw new IllegalArgumentException("qop为auth时，bodyHash不能为空");
            }

            this.credentials.cnonce = credentials.generateClientNonce();

            this.credentials.response = credentials.calculateResponse();

            return credentials;
        }


    }

}
