package priv.szf.fastcall.core.auth.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthPosition;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcJwtAuthProvider;
import priv.szf.fastcall.core.model.credential.JwtCredential;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcJwtAuthHandler extends FcBaseRefreshableAuthHandler implements IFcAuthHandler {

    private final FcJwtAuthProvider authProvider;

    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.JWT;
    }

    @Override
    protected FcJwtAuthProvider getAuthProvider() {
        return authProvider;
    }

    @Override
    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        FcAuthPosition positionOn = ((JwtCredential) credential).getPositionOn();

        String authorization = concatAuthString(credential);
        if (positionOn == FcAuthPosition.HEADER) {
            builder.header(FcHttpHeader.AUTHORIZATION.getName(), authorization);
        }
        else if (positionOn == FcAuthPosition.QUERY) {
            String url = Optional.ofNullable(builder.getUrl$okhttp())
                    .map(HttpUrl::toString)
                    .orElseThrow(() -> new FcUnexpectedException("Url 为空"));
            String newUrl = url + (url.contains("?") ? "&" : "?") +
                    FcHttpHeader.AUTHORIZATION.getName() + "=" + authorization;
            builder.url(newUrl);
        }

        return builder;
    }

}
