package priv.szf.fastcall.core.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.auth.FcRetryManager;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcAuthInterceptor extends FcBaseAuthInterceptor implements Interceptor {

    @Getter
    private final FcAuthHandlerDelegate authHandler;

    private final FcRetryManager retryManager;

    @Override
    protected Response doAfterProceed(Chain chain, Response response) throws IOException {
        if (!retryManager.isFlagAvailable()) {
            return response;
        }
        response.close();
        Request request = response.request();
        Request finalRequest = super.modifyRequest(request);
        Response finalResponse = chain.proceed(finalRequest);
        retryManager.removeFlag();
        return finalResponse;
    }

}
