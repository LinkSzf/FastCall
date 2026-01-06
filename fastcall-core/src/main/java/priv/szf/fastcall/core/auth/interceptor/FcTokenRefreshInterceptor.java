package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.auth.FcRetryManager;

@RequiredArgsConstructor
@Component
public class FcTokenRefreshInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    private final FcRetryManager retryManager;

    @Override
    protected Request modifyRequest(Request request) {
        boolean isRefreshed = getAuthHandler().preRefresh(request);
        return isRefreshed ? super.modifyRequest(request) : request;
    }

    @Override
    protected Response doAfterProceed(Chain chain, Response response) {
        boolean isRefreshedAfterResponse = getAuthHandler().refresh(response);
        if (isRefreshedAfterResponse) {
            retryManager.setFlag();
        }
        return response;
    }
    @Override
    protected boolean shouldNotIntercept(Request request) {
        return !getAuthHandler().isAuthRefreshable(request)
                || retryManager.isFlagAvailable();
    }
}
