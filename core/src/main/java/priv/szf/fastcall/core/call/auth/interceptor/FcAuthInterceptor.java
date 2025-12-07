package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcAuthInterceptor extends FcBaseAuthInterceptor {

    private final FcAuthHandlerDelegate authHandler;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();

        if (isNotAuthNeed(originRequest)) {
            return chain.proceed(originRequest);
        }

        Request newRequest = authHandler.modifyRequest(originRequest);
        return chain.proceed(newRequest);
    }

}
