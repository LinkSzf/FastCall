package io.github.linkszf.fastcall.core.auth.handler;

import lombok.RequiredArgsConstructor;
import io.github.linkszf.fastcall.core.auth.IFcAuthHandler;
import io.github.linkszf.fastcall.common.FcAuthType;

@RequiredArgsConstructor
public class FcTokenAuthHandler extends FcBaseAuthHandler implements IFcAuthHandler {


    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BEARER;
    }

}
