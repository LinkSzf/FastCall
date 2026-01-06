package priv.szf.fastcall.core.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcApiKeyAuthProvider;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.ApiKeyAuthContent;
import priv.szf.fastcall.core.model.credential.ApiKeyCredential;

import java.util.Objects;

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
    public Request modifyRequest(Request request) {
        String system = getSystem(request);
        ApiKeyCredential credential = getCredential(system);

        if (Objects.isNull(credential)) {
            return request;
        }

        String key = credential.getKey();
        String value = credential.getValue();
        ApiKeyAuthContent.In addTo = credential.getAddTo();
        String url = request.url().toString();

        Request.Builder newRequestBuilder = request.newBuilder();
        if (addTo == ApiKeyAuthContent.In.HEADER) {
            newRequestBuilder.addHeader(key, value);
        }
        else if (addTo == ApiKeyAuthContent.In.QUERY) {
            String newUrl = url + (url.contains("?") ? "&" : "?") +
                    key + "=" + value;
            newRequestBuilder.url(newUrl);
        } else {
            throw new FastCallException("ApiKey认证-url[%s]不支持的添加位置[%s]", url, addTo);
        }

        return newRequestBuilder.build();
    }
}
