package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.IFcAuthHandler;
import priv.szf.fastcall.core.call.auth.provider.FcBasicAuthProvider;
import priv.szf.fastcall.core.common.AuthType;

@RequiredArgsConstructor
@Component
public class FcBasicAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    private final FcBasicAuthProvider basicAuthProvider;

    @Override
    public AuthType getAuthType() {
        return AuthType.BASIC;
    }

    @Override
    protected FcBasicAuthProvider getAuthProvider() {
        return basicAuthProvider;
    }

}
