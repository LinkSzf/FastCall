package io.github.linkszf.fastcall.core.auth.handler;

import okhttp3.Request;
import io.github.linkszf.fastcall.core.auth.FcRequestContext;
import io.github.linkszf.fastcall.core.auth.IFcAuthHandler;
import io.github.linkszf.fastcall.common.FcAuthType;

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
