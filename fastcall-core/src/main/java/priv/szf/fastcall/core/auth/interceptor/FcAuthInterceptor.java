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
    public boolean shouldSkip(Request request) {
        FcRequestContext requestContext = getRequestContext(request);
        FcCallType callType = requestContext.getCallType();
        FcAuthType authType = requestContext.getAuthType();
        boolean skipAuth = (callType == FcCallType.ANONYMOUS)
                || (authType == FcAuthType.NONE);
        if (skipAuth) {
            requestContext.getInterceptorContext().setSkipAuth(true);
        }
        return skipAuth;
    }

    @Override
    protected Request doBeforeProceed(Request request) {
        return super.modifyRequest(request);
    }

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response) throws IOException {
        boolean needRetry = getAuthHandler().getRequestContext(request)
                .getInterceptorContext()
                .isRecall();
        if (needRetry) {
            response.close();
            Request retryBaseRequest = FcUtils.rebuildRequestWithBodySnapshot(request);
            Request finalRequest = super.modifyRequest(retryBaseRequest);
            return chain.proceed(finalRequest);
        }
        return response;
    }


}
