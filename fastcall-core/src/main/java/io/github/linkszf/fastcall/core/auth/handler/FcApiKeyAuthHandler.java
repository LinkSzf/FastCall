package io.github.linkszf.fastcall.core.auth.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import io.github.linkszf.fastcall.common.FcAuthPosition;
import io.github.linkszf.fastcall.common.exception.FcUnexpectedException;
import io.github.linkszf.fastcall.common.model.credential.ICredential;
import io.github.linkszf.fastcall.core.auth.IFcAuthHandler;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.core.model.credential.ApiKeyCredential;

import java.util.Optional;

@RequiredArgsConstructor
public class FcApiKeyAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.APIKEY;
    }

    @Override
    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        ApiKeyCredential apiKeyCredential = (ApiKeyCredential) credential;
        String key = apiKeyCredential.getKey();
        String value = apiKeyCredential.getValue();
        FcAuthPosition positionOn = apiKeyCredential.getPositionOn();

        if (positionOn == FcAuthPosition.HEADER) {
            builder.header(key, value);
        }
        else if (positionOn == FcAuthPosition.QUERY) {
            String url = Optional.ofNullable(builder.getUrl$okhttp())
                    .map(HttpUrl::toString)
                    .orElseThrow(() -> new FcUnexpectedException("URL must not be null"));
            String newUrl = url + (url.contains("?") ? "&" : "?") +
                    key + "=" + value;
            builder.url(newUrl);
        }

        return builder;
    }

}
