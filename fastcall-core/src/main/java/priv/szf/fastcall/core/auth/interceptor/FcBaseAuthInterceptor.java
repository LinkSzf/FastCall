package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;

import java.io.IOException;
import java.util.Optional;

@SuperBuilder
public abstract class FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    protected abstract boolean shouldSkip(Request request);

    protected abstract Request doBeforeProceed(Request request);

    protected abstract Response doAfterProceed(Chain chain, Request request, Response response) throws IOException;


    @Override
    public @NonNull Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        if (shouldSkip(originRequest)) {
            return chain.proceed(originRequest);
        }

        Request request = doBeforeProceed(originRequest);

        Response response = chain.proceed(request);

        return doAfterProceed(chain, request, response);
    }

    protected final Request modifyRequest(Request request) {
        return getAuthHandler().modifyRequest(request);
    }

    protected final @NonNull FcRequestContext getRequestContext(Request request) {
        return Optional.ofNullable(request.tag(FcRequestContext.class))
                .orElseThrow(() -> new FcUnexpectedException("Required request context not found"));
    }

}
