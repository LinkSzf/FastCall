package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.common.FcAuthType;

public class FcNoneAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.NONE;
    }

    @Override
    public Request modifyRequest(Request request, FcRequestContext context) {
        return request;
    }

}
