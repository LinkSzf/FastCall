package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.model.auth.BaseDynAuthContent;
import priv.szf.fastcall.core.call.auth.provider.FcDefaultFcAuthProvider;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcMediaType;
import priv.szf.fastcall.core.common.FcRequestMethod;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.FcAuthProp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public abstract class FcBaseTokenAuthHandler<C extends BaseDynAuthContent, T> extends FcBaseAuthHandler<C> {

    protected abstract IFcSource getSource();

    @Override
    public Request modifyRequest(Request request) {
        T token = getToken(request);
        if (Objects.isNull(token)) {
            return request;
        }
        String authorization = getAuthType().getPrefix() + token;
        return request.newBuilder()
                .header(FcHttpHeader.AUTHORIZATION.getName(), authorization)
                .build();
    }

    public boolean isAuthRefreshable(Request request) {
        return true;
    }

    public boolean isAuthPreRefreshable() {
        return true;
    }

    public boolean isInvalidToken(FcTokenPak<?> accessToken) {
        if (Objects.isNull(accessToken)) {
            return true;
        }

        LocalDateTime estimatedExpirationTime = accessToken.getEstimatedExpiration();
        return Objects.isNull(estimatedExpirationTime)
                || estimatedExpirationTime.isBefore(LocalDateTime.now());
    }

    FcTokenPak<T> getAccessToken(Request request) {
        String systemCode = getSystemCode(request);
        return getSource().getAccessToken(systemCode);
    }

    T getToken(Request request) {
        return Optional.ofNullable(getAccessToken(request))
                .map(FcTokenPak<T>::getToken)
                .orElse(null);
    }
    public void refreshToken(Request request, Response response) {
        String systemCode = getSystemCode(request);
        FcTokenPak<T> token = callAndGetToken(request, response, systemCode);
        getSource().updateAccessToken(systemCode, token);
    }

    protected FcTokenPak<T> callAndGetToken(Request request, Response response, String systemCode) {
        IFcAuthProvider<C, T> authProvider = getAuthProvider(systemCode);

        FastCallClient client = FastCallClientFactory.getExistedClient(systemCode);
        C authContent = getAuthContent(request);
        FcAuthPak authInfo = getAuthInfo(request);

        FcAuthProp authProp = Optional.ofNullable(authContent.getProp()).orElse(new FcAuthProp());
        FastCallResponse<String> authResponse = client.newCall(String.class)
                .host(authInfo.getParticularHost())
                .uri(authInfo.getPath())
                .params(authProp.getParams())
                .method(FcRequestMethod.POST)
                .header(FcHttpHeader.CONTENT_TYPE, FcMediaType.APPLICATION_JSON)
                .header(FcHttpHeader.ACCEPT, FcMediaType.APPLICATION_JSON)
                .headers(authProp.getHeaders())
                .body(authProp.getBody())
                .prepared()
                .anonymousCallIt();

        return authProvider.mapToToken(request, authResponse, authContent);
    }


    protected IFcAuthProvider<C, T> getAuthProvider(String systemCode) {
        return new FcDefaultFcAuthProvider<>(systemCode);
    }

    public void doBeforeRefreshToken(Request request, Response response) {
    }
}
