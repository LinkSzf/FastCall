package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import priv.szf.fastcall.core.common.AuthType;

public interface IFcAuthHandler {

    AuthType getAuthType();

    Request modifyRequest(Request request);
}
