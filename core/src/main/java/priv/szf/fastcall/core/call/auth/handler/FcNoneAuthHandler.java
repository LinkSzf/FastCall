package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.IFcAuthHandler;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.common.AuthType;

@Component
public class FcNoneAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    @Override
    public AuthType getAuthType() {
        return AuthType.NONE;
    }

    @Override
    protected IFcAuthProvider<?> getAuthProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Request modifyRequest(Request request) {
        return request;
    }

    @Override
    public boolean isNotAuthNeed(Request request) {
        return true;
    }
}
