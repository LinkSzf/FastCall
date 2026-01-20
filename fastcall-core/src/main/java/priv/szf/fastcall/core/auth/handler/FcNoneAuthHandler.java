package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.common.FcAuthType;

@Component
public class FcNoneAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.NONE;
    }

    @Override
    protected IFcAuthProvider getAuthProvider() {
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
