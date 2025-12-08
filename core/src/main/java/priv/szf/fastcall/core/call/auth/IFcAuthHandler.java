package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public interface IFcAuthHandler {

    AuthType getAuthType();

    FcSourcePak getSourceInfo(Request request);

    Request modifyRequest(Request request);

    default boolean isAuthRefreshable(Request request) {
        return false;
    }

    default boolean isInvalidToken(FcTokenPak accessToken) {
        if (Objects.isNull(accessToken)) {
            return true;
        }

        LocalDateTime estimatedExpirationTime = accessToken.getEstimatedExpirationTime();
        return Objects.isNull(estimatedExpirationTime)
                || estimatedExpirationTime.isBefore(LocalDateTime.now());
    }

    default void refreshToken(FcSourcePak sourceInfo) {}
}
