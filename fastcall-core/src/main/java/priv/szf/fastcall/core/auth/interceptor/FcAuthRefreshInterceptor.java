package priv.szf.fastcall.core.auth.interceptor;

import lombok.experimental.SuperBuilder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.auth.FcRequestContext;

@SuperBuilder
public class FcAuthRefreshInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Override
    protected boolean shouldSkip(FcRequestContext context) {
        FcRequestContext.InterceptorContext interceptorContext = context.getInterceptorContext();
        boolean skipAuth = interceptorContext.isSkipAuth();
        boolean recall = interceptorContext.isRecall();
        boolean nonRefreshable = getAuthSupporter().isAuthNonRefreshable(context.getAuthType());
        return skipAuth|| nonRefreshable || recall;
    }

    @Override
    protected Request doBeforeProceed(Request request, FcRequestContext context) {
        boolean isRefreshed = getAuthSupporter().preRefresh(context);
        return isRefreshed ? super.modifyRequest(request, context) : request;
    }

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response, FcRequestContext context) {
        boolean isRefreshedAfterResponse = getAuthSupporter().refreshIfNecessary(context, response.code());
        if (isRefreshedAfterResponse) {
            getRequestContext(request).getInterceptorContext().setRecall(true);
        }
        return response;
    }

}
