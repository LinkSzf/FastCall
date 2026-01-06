package priv.szf.fastcall.core.auth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcTokenAuthProvider;
import priv.szf.fastcall.common.FcAuthType;

@RequiredArgsConstructor
@Component
public class FcBearerTokenAuthHandler extends FcBaseTokenAuthHandler
        implements IFcRefreshableAuthHandler {

    private final FcTokenAuthProvider tokenAuthProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BEARER;
    }

    @Override
    protected FcTokenAuthProvider getInteractiveAuthProvider() {
        return tokenAuthProvider;
    }

}
