package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcAuthInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response) throws IOException {
        FcRequestContext requestContext = getAuthHandler().getRequestContext(request);
        boolean needRetry = requestContext.getInterceptorContext().isNeedRetry();
        if (!needRetry) {
            return response;
        }
        response.close();
        Request finalRequest = super.modifyRequest(request);
        return chain.proceed(finalRequest);
    }

    @Override
    public boolean shouldSkip(Request request) {
        boolean notAuthNeed = getAuthHandler().isNotAuthNeed(request);
        FcRequestContext requestContext = getAuthHandler().getRequestContext(request);
        boolean anonymousCall = (FcCallType.ANONYMOUS == requestContext.getCallType());
        boolean skip = notAuthNeed || anonymousCall;
        if (skip) {
            requestContext.getInterceptorContext().setSkipAuth(true);
        }
        return skip;
    }

    @Override
    protected Request doBeforeProceed(Request request) {
        return super.modifyRequest(request);
    }


}
