package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.call.auth.FcRetryManager;

@RequiredArgsConstructor
@Component
public class FcTokenRefreshInterceptor extends FcBaseAuthInterceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    private final FcRetryManager retryManager;

    @Override
    protected Request modifyRequest(Request request) {
        boolean isRefreshed = getAuthHandler().preRefreshTokenIfNecessary(request);
        return isRefreshed ? super.modifyRequest(request) : request;
    }

    @Override
    protected Response doAfterProceed(Chain chain, Response response) {
        boolean isRefreshedAfterResponse = getAuthHandler().refreshTokenIfNecessary(response);
        if (isRefreshedAfterResponse) {
            retryManager.setFlag();
        }
        return response;
    }
    @Override
    protected boolean shouldNotIntercept(Request originRequest) {
        return !getAuthHandler().isAuthRefreshable(originRequest)
                || retryManager.isFlagAvailable();
    }
}
