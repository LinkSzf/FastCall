package priv.szf.fastcall.core.auth.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcTokenCredentialProvider;
import priv.szf.fastcall.common.FcAuthType;

@RequiredArgsConstructor
public class FcTokenAuthHandler extends FcBaseRefreshableAuthHandler
        implements IFcRefreshableAuthHandler {

    @Getter
    private final FcTokenCredentialProvider credentialProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BEARER;
    }

}
