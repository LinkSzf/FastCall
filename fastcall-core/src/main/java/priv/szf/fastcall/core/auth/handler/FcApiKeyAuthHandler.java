package priv.szf.fastcall.core.auth.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcApiKeyAuthProvider;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.content.ApiKeyAuthContent;
import priv.szf.fastcall.core.model.credential.ApiKeyCredential;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcApiKeyAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    private final FcApiKeyAuthProvider apiKeyAuthProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.APIKEY;
    }

    @Override
    protected FcApiKeyAuthProvider getAuthProvider() {
        return apiKeyAuthProvider;
    }

    @Override
    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        ApiKeyCredential apiKeyCredential = (ApiKeyCredential) credential;
        String key = apiKeyCredential.getKey();
        String value = apiKeyCredential.getValue();
        ApiKeyAuthContent.In addTo = apiKeyCredential.getAddTo();

        if (addTo == ApiKeyAuthContent.In.HEADER) {
            builder.header(key, value);
        }
        else if (addTo == ApiKeyAuthContent.In.QUERY) {
            String url = Optional.ofNullable(builder.getUrl$okhttp())
                    .map(HttpUrl::toString)
                    .orElseThrow(() -> new FcUnexpectedException("Url 为空"));
            String newUrl = url + (url.contains("?") ? "&" : "?") +
                    key + "=" + value;
            builder.url(newUrl);
        }

        return builder;
    }

}
