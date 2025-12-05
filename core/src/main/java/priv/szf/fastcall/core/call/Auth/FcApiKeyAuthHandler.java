package priv.szf.fastcall.core.call.Auth;

import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.AuthType;
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
        return null;
    }
}
