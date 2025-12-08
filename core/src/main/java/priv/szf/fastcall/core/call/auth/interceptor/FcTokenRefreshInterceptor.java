package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

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

        authHandler.preRefreshTokenIfNecessary(request);

//        if () {
//
//        }
//
//        FcSystemPak system = request.tag(FcSystemPak.class);
//        String systemKey = system != null ? system.getCode() : "default";
//        Object systemLock = systemLocks.computeIfAbsent(systemKey, k -> new Object());
//
//        FcTokenPak tokenPak = request.tag(FcTokenPak.class);
//
//        if (authHandler.isInvalidToken(tokenPak)) {
//            synchronized (systemLock) {
//                if (authHandler.isInvalidToken(tokenPak)) {
//                    authHandler.refreshToken(system, auth);
//                }
//            }
//
//            // 用新token继续调用并返回Response
//        }

        Response response = chain.proceed(request);

//        if (response.code() == 401) {
//            synchronized (systemLock) {
//                // 再次检查，防止其他线程已经刷新了token
//                if (response.code() == 401) {
//                    refreshToken(system);
//                    // 创建新请求并重试
//                    Request newRequest = request.newBuilder()
//                            .header("Authorization", "Bearer " + authHandler.getCurrentToken(system))
//                            .build();
//                    response.close();
//                    return chain.proceed(newRequest);
//                }
//            }
//        }

        return response;
    }

}
