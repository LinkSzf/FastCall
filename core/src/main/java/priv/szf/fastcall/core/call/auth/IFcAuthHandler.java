package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;

public interface IFcAuthHandler {

    AuthType getAuthType();

    FcSourcePak getSourceInfo(Request request);

    String getSystemCode(Request request);

    Request modifyRequest(Request request);
}
