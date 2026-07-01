package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.hutool.jwt.signers.NoneJWTSigner;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import priv.szf.fastcall.common.FcAuthPosition;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.content.JwtAuthContent;
import priv.szf.fastcall.core.auth.IFcDynCredentialProvider;
import priv.szf.fastcall.core.model.credential.JwtCredential;

import java.security.Security;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class FcJwtCredentialProvider extends FcBaseCredentialProvider<JwtAuthContent>
        implements IFcDynCredentialProvider {

    private static final long DEFAULT_EXPIRES_IN = 60 * 60 * 24;


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.JWT;
    }

    @Override
    protected JwtCredential buildCredential(@NonNull JwtAuthContent content) {
        readyContent(content);
        setTime(content);
        String token = createToken(content);
        Long expireTime = (Long) content.getPayload().get("exp");
        return JwtCredential.create(token, expireTime, content.getPositionOn());
    }

    private String createToken(JwtAuthContent content) {
        Map<String, Object> header = content.getHeader();
        Map<String, Object> payload = content.getPayload();
        String algorithm = content.getAlgorithm();
        String secret = content.getSecret();
        JWTSigner signer = createSigner(algorithm, secret);
        return JWTUtil.createToken(header, payload, signer);
    }

    private JWTSigner createSigner(String algorithm, String secret) {
        if (StrUtil.isBlank(algorithm) || NoneJWTSigner.ID_NONE.equals(algorithm)) {
            return JWTSignerUtil.none();
        }

        String keyMaterial = Optional.ofNullable(secret)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new FcUnexpectedException("JWT secret must not be blank"));

        if (StrUtil.startWithIgnoreCase(algorithm, "HS")) {
            return JWTSignerUtil.createSigner(algorithm, keyMaterial.getBytes(StandardCharsets.UTF_8));
        }

        Key privateKey = KeyUtil.generatePrivateKey(algorithm, decodeAsymmetricKey(keyMaterial));
        return JWTSignerUtil.createSigner(algorithm, privateKey);
    }

    private byte[] decodeAsymmetricKey(String keyMaterial) {
        String normalized = keyMaterial.trim();
        if (normalized.startsWith("-----BEGIN")) {
            String pemBody = normalized
                    .replaceAll("-----BEGIN [^-]+-----", "")
                    .replaceAll("-----END [^-]+-----", "")
                    .replaceAll("\\s+", "");
            return Base64.getDecoder().decode(pemBody);
        }
        try {
            return SecureUtil.decode(normalized);
        } catch (Exception e) {
            throw new IllegalArgumentException("Asymmetric JWT key must be PEM, Base64, or Hex encoded", e);
        }
    }

    private void readyContent(JwtAuthContent content) {
        if (Objects.isNull(content.getPositionOn())) {
            content.setPositionOn(FcAuthPosition.HEADER);
        }

        if (Objects.isNull(content.getHeader())) {
            content.setHeader(new HashMap<>());
        }

        if (Objects.isNull(content.getPayload())) {
            content.setPayload(new HashMap<>());
        }
    }

    private void setTime(JwtAuthContent content) {
        long expiresIn = Optional.of(content).map(JwtAuthContent::getExpiresIn).orElse(DEFAULT_EXPIRES_IN);

        Map<String, Object> payload = content.getPayload();
        payload.computeIfAbsent("iat", k -> getNowPlusTime(content.getIssueAtOffset()));
        payload.computeIfAbsent("exp", k -> getNowPlusTime(expiresIn));

        Long notValidBefore = content.getNotValidBefore();
        if (Objects.nonNull(notValidBefore)) {
            payload.computeIfAbsent("nbf", k -> getNowPlusTime(notValidBefore));
        }

    }

    private long getNowPlusTime(Long offset) {
        if (Objects.isNull(offset)) {
            offset = 0L;
        }
        return Instant.now().plusSeconds(offset).getEpochSecond();
    }
}
