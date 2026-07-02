package priv.szf.fastcall.core.auth.interceptor;

import lombok.experimental.SuperBuilder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.IOException;

@SuperBuilder
public class FcAuthInterceptor extends FcBaseAuthInterceptor implements Interceptor {


    @Override
    public boolean shouldSkip(FcRequestContext context) {
        FcCallType callType = context.getCallType();
        FcAuthType authType = context.getAuthType();
        boolean skipAuth = (callType == FcCallType.ANONYMOUS)
                || (authType == FcAuthType.NONE);
        if (skipAuth) {
            context.getInterceptorContext().markSkipAuth();
        }
        return skipAuth;
    }

    @Override
    protected Request doBeforeProceed(Request request, FcRequestContext context) {
        return super.modifyRequest(request, context);
    }

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response, FcRequestContext context) throws IOException {
        boolean needRetry = context.getInterceptorContext().isNeedRetry();
        if (needRetry) {
            response.close();
            Request finalRequest = super.modifyRequest(request, context);
            return chain.proceed(finalRequest);
        }
        return response;
    }


}
