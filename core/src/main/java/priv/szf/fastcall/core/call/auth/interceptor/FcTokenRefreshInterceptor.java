package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class FcTokenRefreshInterceptor implements Interceptor {

    private final FcAuthHandlerDelegate authHandler;

    private final Map<String, Object> systemLocks = new ConcurrentHashMap<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        AuthType authType = request.tag(AuthType.class);
        if (Objects.isNull(authType) || AuthType.isRefreshable(authType)) {
            return chain.proceed(request);
        }

        FcSystemPak system = request.tag(FcSystemPak.class);
        String systemKey = system != null ? system.getCode() : "default";
        Object systemLock = systemLocks.computeIfAbsent(systemKey, k -> new Object());

        FcTokenPak tokenPak = request.tag(FcTokenPak.class);

        if (authHandler.isInvalidToken(tokenPak)) {
            synchronized (systemLock) {
                if (authHandler.isInvalidToken(tokenPak)) {
                    authHandler.refreshToken(system, auth);
                }
            }

            // 用新token继续调用并返回Response
        }

        Response response = chain.proceed(request);

        if (response.code() == 401) {
            synchronized (systemLock) {
                // 再次检查，防止其他线程已经刷新了token
                if (response.code() == 401) {
                    refreshToken(system);
                    // 创建新请求并重试
                    Request newRequest = request.newBuilder()
                            .header("Authorization", "Bearer " + authHandler.getCurrentToken(system))
                            .build();
                    response.close();
                    return chain.proceed(newRequest);
                }
            }
        }

        return response;
    }

    private boolean isInvalidToken(FcTokenPak accessToken) {
        if (Objects.isNull(accessToken)) {
            return true;
        }

        LocalDateTime estimatedExpirationTime = accessToken.getEstimatedExpirationTime();
        return Objects.isNull(estimatedExpirationTime)
                || estimatedExpirationTime.isBefore(LocalDateTime.now());
    }








}
