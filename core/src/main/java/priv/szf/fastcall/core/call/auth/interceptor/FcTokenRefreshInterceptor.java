package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcTokenRefreshInterceptor extends FcBaseAuthInterceptor {

    private final FcAuthHandlerDelegate authHandler;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (isNotAuthNeed(request)
                || !authHandler.isAuthRefreshable(request)
        ) {
            return chain.proceed(request);
        }

        // 访问之前先在本地判断是否token过期，过期则先刷新
        boolean isRefreshed = authHandler.preRefreshTokenIfNecessary(request);
        Request newRequest = isRefreshed ? authHandler.modifyRequest(request) : request;
        Response response = chain.proceed(newRequest);

        // 访问之后判断响应是否返回认证失败，失败则刷新后重试
        boolean isRedo = authHandler.refreshTokenIfNecessary(newRequest, response);
        if (isRedo) {
            response.close();
            Request finalRequest = authHandler.modifyRequest(request);
            return chain.proceed(finalRequest);
        }

        return response;
    }

}
