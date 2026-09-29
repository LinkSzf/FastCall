package io.github.linkszf.fastcall.core.auth.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import io.github.linkszf.fastcall.common.FcAuthPosition;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.FcHttpHeader;
import io.github.linkszf.fastcall.common.exception.FcUnexpectedException;
import io.github.linkszf.fastcall.common.model.credential.ICredential;
import io.github.linkszf.fastcall.core.auth.IFcAuthHandler;
import io.github.linkszf.fastcall.core.model.credential.JwtCredential;

import java.util.Optional;

@RequiredArgsConstructor
public class FcJwtAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {


    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.JWT;
    }

    @Override
    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        FcAuthPosition positionOn = ((JwtCredential) credential).getPositionOn();

        String authorization = concatAuthString(credential);
        if (positionOn == FcAuthPosition.HEADER) {
            builder.header(FcHttpHeader.AUTHORIZATION.getName(), authorization);
        }
        else if (positionOn == FcAuthPosition.QUERY) {
            String url = Optional.ofNullable(builder.getUrl$okhttp())
                    .map(HttpUrl::toString)
                    .orElseThrow(() -> new FcUnexpectedException("URL must not be null"));
            String newUrl = url + (url.contains("?") ? "&" : "?") +
                    FcHttpHeader.AUTHORIZATION.getName() + "=" + authorization;
            builder.url(newUrl);
        }

        return builder;
    }

}
