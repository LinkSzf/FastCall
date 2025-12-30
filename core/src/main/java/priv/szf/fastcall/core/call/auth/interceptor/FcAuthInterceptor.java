package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.call.auth.FcRetryManager;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcAuthInterceptor extends FcBaseAuthInterceptor {

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
