package io.github.linkszf.fastcall.core.model.credential;

import lombok.AllArgsConstructor;
import lombok.Getter;
import io.github.linkszf.fastcall.common.FcAuthPosition;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

import java.time.Instant;
import java.util.Objects;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JwtCredential extends BaseCredential implements ICredential {

    private final String token;

    private final Long expireTime;

    @Getter
    private final FcAuthPosition positionOn;


    public static JwtCredential create(String token, long expiresTime, FcAuthPosition positionOn) {
        return new JwtCredential(token, expiresTime, positionOn);
    }

    @Override
    public String getAuthString() {
        return token;
    }

    @Override
    public boolean isValid() {
        return Objects.nonNull(expireTime)
                && Instant.now().getEpochSecond() < expireTime;
    }
}
