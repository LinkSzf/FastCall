package priv.szf.fastcall.core.call.auth.interceptor;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.handler.FcTokenStatus;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FcTokenRefreshInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        FcTokenStatus tokenStatus = request.tag(FcTokenStatus.class);
        if (tokenStatus == FcTokenStatus.INVALID) {
            refreshToken();

            // 用新token继续调用并返回Response
        }


        Response response = chain.proceed(request);

        // 检测到令牌失效（HTTP 401）
        if (response.code() == 401) {
            synchronized (this) {

                String newToken = authClient.fetchAccessToken();
                authInterceptor.setAccessToken(newToken);


                Request newRequest = request.newBuilder()
                        .header("Authorization", "Bearer " + newToken)
                        .build();
                response.close();
                return chain.proceed(newRequest);
            }
        }
        return response;
    }

    private void refreshToken() {
        // TODO
    }








}
