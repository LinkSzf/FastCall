//package io.github.linkszf.fastcall.core.auth.handler;
//
//import lombok.RequiredArgsConstructor;
//import okhttp3.Request;
//import org.springframework.stereotype.Component;
//import io.github.linkszf.fastcall.core.auth.IFcRefreshableAuthHandler;
//import io.github.linkszf.fastcall.core.auth.provider.FcDigestAuthProvider;
//import io.github.linkszf.fastcall.common.FcAuthType;
//
//@RequiredArgsConstructor
//@Component
//public class FcDigestAuthHandler extends FcBaseRefreshableAuthHandler
//        implements IFcRefreshableAuthHandler {
//
//    private final FcDigestAuthProvider authProvider;
//
//    @Override
//    public FcAuthType getAuthType() {
//        return FcAuthType.DIGEST;
//    }
//
//    @Override
//    protected FcDigestAuthProvider getAuthProvider() {
//        return authProvider;
//    }
//
//    @Override
//    public boolean preRefresh(Request request) {
//        return false;
//    }
//
//
//}
