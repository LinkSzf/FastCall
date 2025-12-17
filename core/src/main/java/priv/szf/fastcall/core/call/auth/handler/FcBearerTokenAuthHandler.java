package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BearerTokenAuth;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FcBearerTokenAuthHandler extends FcBaseTokenAuthHandler<BearerTokenAuth> {

    private final IFcSource source;

    @Override
    public AuthType getAuthType() {
        return AuthType.BEARER;
    }

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    public boolean isAuthRefreshable(Request request) {
        BearerTokenAuth authContent = getAuthContent(request);
        String fixedToken = authContent.getFixedToken();
        if (Objects.nonNull(fixedToken)) {
            return false;
        }

        return super.isAuthRefreshable(request);
    }

    @Override
    String getToken(Request request) {
        BearerTokenAuth authContent = getAuthContent(request);
        String fixedToken = authContent.getFixedToken();
        if (Objects.nonNull(fixedToken)) {
            return fixedToken;
        }
        return super.getToken(request);
    }

}
