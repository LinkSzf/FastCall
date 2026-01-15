package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcAuthInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response) throws IOException {
        boolean needRetry = getAuthHandler().getRequestContext(request)
                .getInterceptorContext()
                .isNeedRetry();
        if (needRetry) {
            response.close();
            Request finalRequest = super.modifyRequest(request);
            return chain.proceed(finalRequest);
        }
        return response;
    }

    @Override
    public boolean shouldSkip(Request request) {
        boolean skip = getAuthHandler().isNotAuthNeed(request);
        if (skip) {
            getAuthHandler().getRequestContext(request)
                    .getInterceptorContext()
                    .setSkipAuth(true);
        }
        return skip;
    }

    @Override
    protected Request doBeforeProceed(Request request) {
        return super.modifyRequest(request);
    }


}
