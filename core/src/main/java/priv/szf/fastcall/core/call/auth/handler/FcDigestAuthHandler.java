package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.call.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.core.common.AuthType;

@RequiredArgsConstructor
@Component
public class FcDigestAuthHandler extends FcBaseTokenAuthHandler
        implements IFcRefreshableAuthHandler {

    private final FcDigestAuthProvider authProvider;

    @Override
    public AuthType getAuthType() {
        return AuthType.DIGEST;
    }

    @Override
    protected FcDigestAuthProvider getInteractiveAuthProvider() {
        return authProvider;
    }

    @Override
    public void preRefresh(Request request) {
    }


}
