package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import priv.szf.fastcall.core.call.source.IFcSource;
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

    FcTokenPak getAccessToken(Request request) {
        String systemCode = getSystemCode(request);
        return getSource().getAccessToken(systemCode);
    }

    String getToken(Request request) {
        return getAccessToken(request).getToken();
    }


}
