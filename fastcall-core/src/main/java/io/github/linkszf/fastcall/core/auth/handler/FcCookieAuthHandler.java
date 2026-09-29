package io.github.linkszf.fastcall.core.auth.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.FcHttpHeader;
import io.github.linkszf.fastcall.core.auth.IFcAuthHandler;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

@RequiredArgsConstructor
public class FcCookieAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {


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
