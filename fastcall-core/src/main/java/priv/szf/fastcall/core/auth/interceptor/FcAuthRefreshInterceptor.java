package priv.szf.fastcall.core.auth.interceptor;

import lombok.experimental.SuperBuilder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.auth.FcRequestContext;

import java.io.IOException;
import java.util.Optional;

@SuperBuilder
public class FcAuthRefreshInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    private static final int DEFAULT_UNAUTHORIZED_CODE = 401;

    @Override
    protected boolean shouldSkip(FcRequestContext context) {
        FcRequestContext.InterceptorContext interceptorContext = context.getInterceptorContext();
        boolean skipAuth = interceptorContext.isSkipAuth();
        boolean recall = interceptorContext.isRecall();
        boolean notRefreshable = getAuthSupporter().isAuthNotRefreshable(context.getAuthType());
        return skipAuth|| notRefreshable || recall;
    }

    @Override
    protected Request doBeforeProceed(Request request, FcRequestContext context) {
        boolean isRefreshed = getAuthSupporter().refresh(context);
        return isRefreshed ? super.modifyRequest(request, context) : request;
    }

    @Override
    protected Response doAfterProceed(Chain chain, Request request, Response response, FcRequestContext context) throws IOException {
        if (isAuthorised(context, response)) {
            return response;
        }

        getAuthSupporter().invalidateCredential(context);
        boolean isRefreshedAfterResponse = getAuthSupporter().refresh(context);
        if (isRefreshedAfterResponse) {
            getRequestContext(request).getInterceptorContext().setRecall(true);
        }
        return response;
    }

    private boolean isAuthorised(FcRequestContext context, Response response) {
        int unauthorizedCode = Optional.of(context)
                .map(FcRequestContext::getSource)
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getUnauthorizedCode)
                .orElse(DEFAULT_UNAUTHORIZED_CODE);
        return response.code() != unauthorizedCode;
    }

}
