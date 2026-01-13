package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;

@RequiredArgsConstructor
@Component
public class FcAuthRefreshInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    @Override
    protected boolean shouldSkip(Request request) {
        FcRequestContext.InterceptorContext interceptorContext = getAuthHandler().getRequestContext(request)
                .getInterceptorContext();
        boolean skipAuth = interceptorContext.isSkipAuth();
        boolean needRetry = interceptorContext.isNeedRetry();
        boolean authRefreshable = getAuthHandler().isAuthRefreshable(request);
        return skipAuth || needRetry || !authRefreshable;
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
            getAuthHandler().getRequestContext(request).getInterceptorContext().setNeedRetry(true);
        }
        return response;
    }

}
