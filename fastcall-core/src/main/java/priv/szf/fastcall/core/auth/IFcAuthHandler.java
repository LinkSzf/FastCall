package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import priv.szf.fastcall.common.FcAuthType;

public interface IFcAuthHandler {

    FcAuthType getAuthType();

    Request modifyRequest(Request request);

    FcRequestContext getRequestContext(Request request);


}
