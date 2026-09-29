package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.IOException;
import java.util.Optional;

/**
 * Abstract OkHttp interceptor template for auth: resolves the mandatory {@code FcRequestContext} request tag,
 * then runs the subclass skip, rewrite (through {@code FcAuthSupporter}) and response hooks.
 */
@SuperBuilder
public abstract class FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthSupporter authSupporter;

    protected abstract boolean shouldSkip(FcRequestContext context);

    protected abstract Request doBeforeProceed(Request request, FcRequestContext context);

    protected abstract Response doAfterProceed(Chain chain, Request request, Response response, FcRequestContext context) throws IOException;


    @Override
    public @NonNull Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        FcRequestContext context = getRequestContext(originRequest);
        if (shouldSkip(context)) {
            return chain.proceed(originRequest);
        }

        Request request = doBeforeProceed(originRequest, context);

        Response response = chain.proceed(request);

        return doAfterProceed(chain, request, response, context);
    }

    protected final Request modifyRequest(Request request, FcRequestContext context) {
        return getAuthSupporter().modifyRequest(request, context);
    }

    protected final @NonNull FcRequestContext getRequestContext(Request request) {
        return Optional.ofNullable(request.tag(FcRequestContext.class))
                .orElseThrow(() -> new FcUnexpectedException("Required request context not found"));
    }

}
