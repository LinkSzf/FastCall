package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.call.auth.FcCallType;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcAuthInceptor implements Interceptor {

    private final FcAuthHandlerDelegate authHandler;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        FcCallType callType = originRequest.tag(FcCallType.class);

        // 对认证接口的访问直接放行
        if (callType == FcCallType.AUTH) {
            return chain.proceed(originRequest);
        }

        Request newRequest = authHandler.getNewRequest(originRequest);

//        Request.Builder builder = originRequest.newBuilder();
//        FastCallClientFactory bean = SpringUtil.getBean(FastCallClientFactory.class);
//
//
//        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

}
