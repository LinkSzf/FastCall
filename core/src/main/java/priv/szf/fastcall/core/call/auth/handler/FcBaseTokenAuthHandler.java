package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

public abstract class FcBaseTokenAuthHandler<T extends BaseAuthContent> extends FcBaseAuthHandler<T> {

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
    public void refreshToken(FcSourcePak sourceInfo) {
        FcSystemPak system = sourceInfo.getSystem();
        String systemCode = system.getCode();
        FcTokenPak accessToken = sourceInfo.getAccessToken();
        FcAuthPak auth = sourceInfo.getAuth();

        FastCallClient client = FastCallClientFactory.getExistedClient(systemCode);
        FcTokenPak token = client.doAuth();
//        getSource().updateAccessToken(systemCode, token);
    }
}
