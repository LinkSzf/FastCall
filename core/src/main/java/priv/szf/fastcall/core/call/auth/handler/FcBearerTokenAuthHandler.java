package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BearerTokenAuth;

@RequiredArgsConstructor
@Component
public class FcBearerTokenAuthHandler extends FcBaseTokenAuthHandler<BearerTokenAuth> {

    private final IFcSource source;

    @Override
    public AuthType getAuthType() {
        return AuthType.BEARER;
    }

    @Override
    IFcSource getSource() {
        return source;
    }

//    @Override
//    public Request modifyRequest(Request request) {
//        FcTokenPak accessToken = getAccessToken(request);
//        String authorization = getAuthType().getPrefix() + accessToken.getToken();
//        return request.newBuilder()
//                .header("Authorization", authorization)
//                .build();
//    }



}
