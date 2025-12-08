package priv.szf.fastcall.core.call.auth.handler;

import cn.hutool.core.util.URLUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;
import priv.szf.fastcall.core.call.auth.BaseDynAuthContent;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.FcRequestMethod;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;

public abstract class FcBaseTokenAuthHandler<T extends BaseDynAuthContent> extends FcBaseAuthHandler<T> {

    abstract IFcSource getSource();

    @Override
    public Request modifyRequest(Request request) {
        String token = getToken(request);

        String authorization = getAuthType().getPrefix() + token;
        return request.newBuilder()
                .header("Authorization", authorization)
                .build();
    }

    @Override
    public boolean isAuthRefreshable(Request request) {
        return true;
    }

    FcTokenPak getAccessToken(Request request) {
        String systemCode = getSystemCode(request);
        return getSource().getAccessToken(systemCode);
    }

    String getToken(Request request) {
        return getAccessToken(request).getToken();
    }

    @Override
    public void refreshToken(Request request) {
        String systemCode = getSystemCode(request);
        FastCallClient client = FastCallClientFactory.getExistedClient(systemCode);

        FcSourcePak sourceInfo = getSourceInfo(request);
        T authContent = getAuthContent(request);
        FcTokenPak token = client.<FcTokenPak>url(getUrl(sourceInfo))
                .method(FcRequestMethod.POST)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(getAuthBody(authContent))
                .anonymousCall();

        getSource().updateAccessToken(systemCode, token);
    }

    Object getAuthBody(T authContent) {
        return authContent.getParams();
    }

    String getUrl(FcSourcePak source) {
        FcAuthPak auth = source.getAuth();
        FcSystemPak system = source.getSystem();
        String host = StringUtils.isBlank(auth.getParticularHost()) ? system.getHost() : auth.getParticularHost();
        return URLUtil.completeUrl(host, auth.getPath());
    }
}
