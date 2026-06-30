package priv.szf.fastcall.core.auth.handler;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcCookieCredentialProvider;
import priv.szf.fastcall.common.model.credential.ICredential;

@RequiredArgsConstructor
@Component
public class FcCookieAuthHandler extends FcBaseRefreshableAuthHandler
        implements IFcRefreshableAuthHandler {

    @Getter
    private final FcCookieCredentialProvider credentialProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.COOKIE;
    }

    @Override
    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        String cookie = credential.getAuthString();
        return builder.header(FcHttpHeader.COOKIE.getName(), cookie);
    }
}
