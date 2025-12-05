package priv.szf.fastcall.core.call.Auth;

import okhttp3.Request;
import priv.szf.fastcall.core.common.AuthType;

public interface IFcAuthHandler {

    AuthType getAuthType();

    Request modifyRequest(Request request);
}
