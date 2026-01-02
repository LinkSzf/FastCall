package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.call.auth.provider.FcTokenAuthProvider;
import priv.szf.fastcall.core.common.AuthType;

@RequiredArgsConstructor
@Component
public class FcBearerTokenAuthHandler extends FcBaseTokenAuthHandler
        implements IFcRefreshableAuthHandler {

    private final FcTokenAuthProvider tokenAuthProvider;

    @Override
    public AuthType getAuthType() {
        return AuthType.BEARER;
    }

    @Override
    protected FcTokenAuthProvider getInteractiveAuthProvider() {
        return tokenAuthProvider;
    }

}
