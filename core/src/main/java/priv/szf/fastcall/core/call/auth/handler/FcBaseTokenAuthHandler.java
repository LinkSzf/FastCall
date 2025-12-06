package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class FcBaseTokenAuthHandler<T extends BaseAuthContent> extends FcBaseAuthHandler<T> {

    private static final String INVALID_TOKEN = FcTokenStatus.INVALID.name();

    abstract IFcSource getSource();

    @Override
    public Request modifyRequest(Request request) {
        String token = getToken(request);

        if (INVALID_TOKEN.equals(token)) {
            return request.newBuilder()
                    .tag(FcTokenStatus.class, FcTokenStatus.INVALID)
                    .build();
        }

        String authorization = getAuthType().getPrefix() + token;
        return request.newBuilder()
                .header("Authorization", authorization)
                .build();
    }

    FcTokenPak getAccessToken(Request request) {
        String systemCode = getSystemCode(request);
        return getSource().getAccessToken(systemCode);
    }

    String getToken(Request request) {
        FcTokenPak accessToken = getAccessToken(request);

        if (isTokenValid(accessToken)) {
            return INVALID_TOKEN;
        }

        return accessToken.getToken();
    }

    private boolean isTokenValid(FcTokenPak accessToken) {
        if (Objects.isNull(accessToken)) {
            return false;
        }

        LocalDateTime estimatedExpirationTime = accessToken.getEstimatedExpirationTime();
        return Objects.isNull(estimatedExpirationTime)
                || estimatedExpirationTime.isBefore(LocalDateTime.now());
    }


}
