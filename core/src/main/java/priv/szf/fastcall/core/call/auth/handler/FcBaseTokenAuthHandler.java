package priv.szf.fastcall.core.call.auth.handler;

import cn.hutool.core.util.URLUtil;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.call.auth.BaseDynAuthContent;
import priv.szf.fastcall.core.call.auth.provider.FcDefaultFcAuthProvider;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcMediaType;
import priv.szf.fastcall.core.common.FcRequestMethod;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;

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

    public boolean isInvalidToken(FcTokenPak<T> accessToken) {
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
        FcSourcePak sourceInfo = getSourceInfo(request);
        C authContent = getAuthContent(request);

        FastCallResponse<String> authResponse = client.newCall(String.class)
                .url(getUrl(sourceInfo))
                .method(FcRequestMethod.POST)
                .header(FcHttpHeader.CONTENT_TYPE, FcMediaType.APPLICATION_JSON)
                .header(FcHttpHeader.ACCEPT, FcMediaType.APPLICATION_JSON)
                .body(authProvider.getRequestBody(authContent))
                .prepared()
                .anonymousCall();

        return authProvider.mapToToken(request, authResponse, authContent);
    }


    protected IFcAuthProvider<C, T> getAuthProvider(String systemCode) {
        return new FcDefaultFcAuthProvider<>(systemCode);
    }

    protected String getUrl(FcSourcePak source) {
        FcAuthPak auth = source.getAuth();
        FcSystemPak system = source.getSystem();
        String host = StringUtils.isBlank(auth.getParticularHost()) ? system.getHost() : auth.getParticularHost();
        return URLUtil.completeUrl(host, auth.getPath());
    }
}
