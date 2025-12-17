package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.time.LocalDateTime;
import java.util.Objects;

public interface IFcAuthHandler {

    AuthType getAuthType();

    FcSourcePak getSourceInfo(Request request);

    String getSystemCode(Request request);

    Request modifyRequest(Request request);

    default boolean isAuthRefreshable(Request request) {
        return false;
    }

    default boolean isAuthPreRefreshable() {
        return true;
    }

    default boolean isInvalidToken(FcTokenPak accessToken) {
        if (Objects.isNull(accessToken)) {
            return true;
        }

        LocalDateTime estimatedExpirationTime = accessToken.getEstimatedExpiration();
        return Objects.isNull(estimatedExpirationTime)
                || estimatedExpirationTime.isBefore(LocalDateTime.now());
    }

    default void refreshToken(Request request, Response response) {}
}
