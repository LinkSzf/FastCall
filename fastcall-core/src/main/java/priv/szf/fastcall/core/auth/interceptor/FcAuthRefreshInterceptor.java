package priv.szf.fastcall.core.auth.interceptor;

import lombok.experimental.SuperBuilder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.auth.FcRequestContext;

@SuperBuilder
public class FcAuthRefreshInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Override
    protected boolean shouldSkip(Request request) {
        FcRequestContext requestContext = getRequestContext(request);
        FcRequestContext.InterceptorContext interceptorContext = requestContext.getInterceptorContext();
        boolean skipAuth = interceptorContext.isSkipAuth();
        boolean recall = interceptorContext.isRecall();
        boolean nonRefreshable = !getAuthHandler().isAuthRefreshable(request);
        return skipAuth|| nonRefreshable || recall;
    }

    @Override
    protected Request doBeforeProceed(Request request) {
        boolean isRefreshed = getAuthHandler().preRefresh(request);
        return isRefreshed ? super.modifyRequest(request) : request;
    }

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response) {
        boolean isRefreshedAfterResponse = getAuthHandler().refreshIfNecessary(request, response);
        if (isRefreshedAfterResponse) {
            getRequestContext(request).getInterceptorContext().setRecall(true);
        }
        return response;
    }

}
