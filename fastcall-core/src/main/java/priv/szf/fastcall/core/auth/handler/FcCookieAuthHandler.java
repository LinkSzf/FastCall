package priv.szf.fastcall.core.auth.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcCookieAuthProvider;
import priv.szf.fastcall.core.model.credential.ICredential;

@RequiredArgsConstructor
@Component
public class FcCookieAuthHandler extends FcBaseRefreshableAuthHandler
        implements IFcRefreshableAuthHandler {

    private final FcCookieAuthProvider authProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.COOKIE;
    }

    @Override
    protected FcCookieAuthProvider getInteractiveAuthProvider() {
        return authProvider;
    }

    @Override
    protected Request doModifyRequest(@NonNull Request request, @NonNull ICredential credential) {
        String cookie = credential.getAuthString();
        return request.newBuilder()
                .addHeader(FcHttpHeader.COOKIE.getName(), cookie)
                .build();
    }
}
