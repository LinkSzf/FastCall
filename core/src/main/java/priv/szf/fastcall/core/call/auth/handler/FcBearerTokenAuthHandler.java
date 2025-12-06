package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BearerTokenAuth;

@RequiredArgsConstructor
@Component
public class FcBearerTokenAuthHandler extends FcBaseAuthHandler<BearerTokenAuth> {

    private final IFcSource source;

    @Override
    public AuthType getAuthType() {
        return AuthType.BEARER;
    }

    @Override
    public Request modifyRequest(Request request) {
        String token = getContent(request).getToken();
        String authorization = getAuthType().getPrefix() + token;
        return request.newBuilder()
                .header("Authorization", authorization)
                .build();
    }
}
