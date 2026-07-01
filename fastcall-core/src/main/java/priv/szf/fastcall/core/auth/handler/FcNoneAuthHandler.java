package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.common.FcAuthType;

public class FcNoneAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.NONE;
    }

    @Override
    public Request modifyRequest(Request request) {
        return request;
    }

    @Override
    protected IFcCredentialProvider getCredentialProvider() {
        throw new UnsupportedOperationException();
    }
}
