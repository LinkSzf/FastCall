package priv.szf.fastcall.core.call.auth.provider.digest;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.Digester;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.util.Objects;

@Getter
public class DigestCredential {

    private String username;

    private String password;

    private String realm;

    private String nonce;

    private String uri;

    private String method;

    private String opaque;

    private String qop;

    private DigestAlgorithm algorithm = DigestAlgorithm.MD5;

    private String cnonce;

    private String response;

    private int nc;

    private String bodyHash;

    private String system;

    private String credentialStr;

    public static Builder builder() {
        return new Builder();
    }

    public String buildCredentialStr() {
        if (Objects.nonNull(credentialStr) && !existQop()) {
            return this.credentialStr;
        }

        if (existQop()) {
            this.nc = ClientNonceManager.system(system).getNextNc(nonce);
        }
        this.cnonce = generateClientNonce();
        this.response = calculateResponse();
        StringBuilder credential = new StringBuilder();

        credential.append("username=\"").append(username).append("\", ");
        credential.append("realm=\"").append(realm).append("\", ");
        credential.append("nonce=\"").append(nonce).append("\", ");
        credential.append("uri=\"").append(uri).append("\", ");

        if (Objects.nonNull(algorithm)) {
            credential.append("algorithm=").append(algorithm).append(", ");
        }

        if (existQop()) {
            String ncString = String.format("%08x", nc);
            credential.append("qop=").append(qop).append(", ");
            credential.append("nc=").append(ncString).append(", ");
            credential.append("cnonce=\"").append(cnonce).append("\", ");
        }

        credential.append("response=\"").append(response).append("\"");

        if (StringUtils.isNotBlank(opaque)) {
            credential.append(", opaque=\"").append(opaque).append("\"");
        }
        this.credentialStr = credential.toString();
        return this.credentialStr;
    }

    private boolean existQop() {
        return StringUtils.isNotEmpty(qop);
    }

    private String calculateResponse() {
        String ha1 = calculateHA1();
        String ha2 = calculateHA2();

        String text = existQop() ?
                StringUtils.joinWith(":", ha1, nonce, String.format("%08x", nc), cnonce, qop, ha2)
                : StringUtils.joinWith(":", ha1, nonce, ha2);

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

        private final DigestCredential credential = new DigestCredential();

        public Builder username(String username) {
            this.credential.username = username;
            return this;
        }

        public Builder password(String password) {
            this.credential.password = password;
            return this;
        }

        public Builder challenge(DigestChallenge challenge) {
            this.credential.realm = challenge.getRealm();
            this.credential.nonce = challenge.getNonce();
            this.credential.opaque = challenge.getOpaque();

            if (StringUtils.isNotEmpty(challenge.getQop())) {
                this.credential.qop = challenge.getQop();
            }

            if (StringUtils.isNotEmpty(challenge.getAlgorithm())) {
                this.credential.algorithm = DigestAlgorithm.fromString(challenge.getAlgorithm());
            }

            return this;
        }

        public Builder uri(String uri) {
            this.credential.uri = uri;
            return this;
        }

        public Builder method(String method) {
            this.credential.method = method;
            return this;
        }
        public Builder body(byte[] body) {
            if (Objects.isNull(body)) {
                body = new byte[0];
            }
            this.credential.bodyHash = credential.hash(new String(body));
            return this;
        }

        public Builder system(String system) {
            this.credential.system = system;
            return this;
        }

        public DigestCredential build() {
            if (StringUtils.isAnyEmpty(credential.username, credential.password)) {
                throw new IllegalArgumentException("用户名和密码不能为空");
            }

            if (StringUtils.isAnyEmpty(credential.realm, credential.nonce)) {
                throw new IllegalArgumentException("realm和nonce不能为空");
            }

            if (StringUtils.isAnyEmpty(credential.uri, credential.method)) {
                throw new IllegalArgumentException("URI和HTTP方法不能为空");
            }

            if ("auth-int".equals(credential.qop) && StringUtils.isEmpty(credential.bodyHash)) {
                throw new IllegalArgumentException("qop为auth-int时，bodyHash不能为空");
            }

            return credential;
        }


    }

}
