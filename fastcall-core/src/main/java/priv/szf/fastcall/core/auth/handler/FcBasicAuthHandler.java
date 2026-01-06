package priv.szf.fastcall.core.auth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcBasicAuthProvider;
import priv.szf.fastcall.common.FcAuthType;

@RequiredArgsConstructor
@Component
public class FcBasicAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    private final FcBasicAuthProvider basicAuthProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BASIC;
    }

    @Override
    protected FcBasicAuthProvider getAuthProvider() {
        return basicAuthProvider;
    }

}
