package priv.szf.fastcall.core.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.common.FcAuthType;

@RequiredArgsConstructor
@Component
public class FcDigestAuthHandler extends FcBaseRefreshableAuthHandler
        implements IFcRefreshableAuthHandler {

    private final FcDigestAuthProvider authProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.DIGEST;
    }

    @Override
    protected FcDigestAuthProvider getInteractiveAuthProvider() {
        return authProvider;
    }

    @Override
    public boolean preRefresh(Request request) {
        return false;
    }


}
