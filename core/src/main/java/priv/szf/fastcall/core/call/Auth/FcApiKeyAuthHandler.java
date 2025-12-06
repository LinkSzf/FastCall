package priv.szf.fastcall.core.call.Auth;

import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.auth.ApiKeyAuth;

@Component
public class FcApiKeyAuthHandler extends FcBaseAuthHandler<ApiKeyAuth> {

    @Override
    public AuthType getAuthType() {
        return AuthType.APIKEY;
    }

    @Override
    public Request modifyRequest(Request request) {
        ApiKeyAuth content = getContent(request);

        String key = content.getKey();
        String value = content.getValue();
        ApiKeyAuth.In addTo = content.getAddTo();
        String url = request.url().toString();

        Request.Builder newRequestBuilder = request.newBuilder();
        if (addTo == ApiKeyAuth.In.HEADER) {
            newRequestBuilder.addHeader(key, value);
        }
        else if (addTo == ApiKeyAuth.In.QUERY) {
            String newUrl = url + (url.contains("?") ? "&" : "?") +
                    key + "=" + value;
            newRequestBuilder.url(newUrl);
        } else {
            throw new FcUnexpectedException(String.format("ApiKey认证-url[%s]不支持的添加位置[%s]", url, addTo));
        }

        return newRequestBuilder.build();
    }
}
