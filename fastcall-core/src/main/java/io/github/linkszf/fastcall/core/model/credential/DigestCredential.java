package io.github.linkszf.fastcall.core.model.credential;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.Digester;
import io.github.linkszf.fastcall.common.model.credential.ICredential;
import io.github.linkszf.fastcall.core.auth.provider.digest.ClientNonceManager;
import io.github.linkszf.fastcall.core.auth.provider.digest.DigestAlgorithm;
import io.github.linkszf.fastcall.core.auth.provider.digest.DigestChallenge;

import java.security.SecureRandom;
import java.util.Objects;

public class DigestCredential extends BaseCredential implements ICredential {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String username;

    private String password;

    private String realm;

    private String nonce;

    private String uri;

    private String method;

    private String opaque;

    private String qop;

    private DigestAlgorithm algorithm = DigestAlgorithm.MD5;

    private String bodyHash;

    private volatile String credentialStr;

    private ClientNonceManager nonceManager;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getAuthString() {
        if (existQop()) {
            return buildCredentialStr();
        }

        String cached = this.credentialStr;
        if (Objects.nonNull(cached)) {
            return cached;
        }

        synchronized (this) {
            if (Objects.isNull(this.credentialStr)) {
                this.credentialStr = buildCredentialStr();
            }
            return this.credentialStr;
        }
    }

    @Override
    public boolean isValid() {
        return this.nonceManager.exist(this.nonce);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.nonceManager.remove(this.nonce);
    }

    private String buildCredentialStr() {
        int nonceCount = existQop()
                ? this.nonceManager.getNextNc(nonce)
                : 0;
        String clientNonce = generateClientNonce();
        String digestResponse = calculateResponse(clientNonce, nonceCount);
        StringBuilder credentialBuilder = new StringBuilder();

        credentialBuilder.append("username=\"").append(username).append("\", ");
        credentialBuilder.append("realm=\"").append(realm).append("\", ");
        credentialBuilder.append("nonce=\"").append(nonce).append("\", ");
        credentialBuilder.append("uri=\"").append(uri).append("\", ");

        if (Objects.nonNull(algorithm)) {
            credentialBuilder.append("algorithm=").append(algorithm).append(", ");
        }

        if (existQop()) {
            String ncString = String.format("%08x", nonceCount);
            credentialBuilder.append("qop=").append(qop).append(", ");
            credentialBuilder.append("nc=").append(ncString).append(", ");
            credentialBuilder.append("cnonce=\"").append(clientNonce).append("\", ");
        }

        credentialBuilder.append("response=\"").append(digestResponse).append("\"");

        if (StrUtil.isNotBlank(opaque)) {
            credentialBuilder.append(", opaque=\"").append(opaque).append("\"");
        }

        return credentialBuilder.toString();
    }

    private boolean existQop() {
        return StrUtil.isNotEmpty(qop);
    }

    private String calculateResponse(String clientNonce, int nonceCount) {
        String ha1 = calculateHA1(clientNonce);
        String ha2 = calculateHA2();

        String text = existQop() ?
                StrUtil.join(":", ha1, nonce, String.format("%08x", nonceCount), clientNonce, qop, ha2)
                : StrUtil.join(":", ha1, nonce, ha2);

        return hash(text);
    }

    private String calculateHA1(String clientNonce) {
        String text = StrUtil.join(":", username, realm, password);
        String ha1 = hash(text);

        if (algorithm.isSessionAlgorithm()) {
            String newText = StrUtil.join(":", ha1, nonce, clientNonce);
            ha1 = hash(newText);
        }

        return ha1;
    }

    private String calculateHA2() {
        String text = ("auth-int".equals(qop)) ?
                StrUtil.join(":", method, uri, bodyHash)
                : StrUtil.join(":", method, uri);

        return hash(text);
    }

    private String generateClientNonce() {
        byte[] bytes = new byte[8];
        SECURE_RANDOM.nextBytes(bytes);
        return HexUtil.encodeHexStr(bytes);
    }

    private String hash(String text) {
        Digester md = new Digester(algorithm.getHashAlgorithm());

        // Handle the SHA-512-256 special case (a truncated SHA-512)
        if (algorithm == DigestAlgorithm.SHA_512_256) {
            byte[] hash = md.digest(text.getBytes());
            // Truncate to 256 bits (32 bytes)
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

            if (StrUtil.isNotEmpty(challenge.getQop())) {
                this.credential.qop = challenge.getQop();
            }

            if (StrUtil.isNotEmpty(challenge.getAlgorithm())) {
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

        public Builder nonceManager(ClientNonceManager clientNonceManager) {
            this.credential.nonceManager = clientNonceManager;
            return this;
        }

        public DigestCredential build() {
            if (StrUtil.hasEmpty(credential.username, credential.password)) {
                throw new IllegalArgumentException("Username and password must not be null");
            }

            if (StrUtil.hasEmpty(credential.realm, credential.nonce)) {
                throw new IllegalArgumentException("realm and nonce must not be null");
            }

            if (StrUtil.hasEmpty(credential.uri, credential.method)) {
                throw new IllegalArgumentException("URI and HTTP method must not be null");
            }

            if ("auth-int".equals(credential.qop) && StrUtil.isEmpty(credential.bodyHash)) {
                throw new IllegalArgumentException("bodyHash must not be null when qop is auth-int");
            }

            return credential;
        }


    }

}
