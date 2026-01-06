package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import priv.szf.fastcall.common.FcAuthType;

public interface IFcAuthHandler {

    FcAuthType getAuthType();

    String getSystem(Request request);

    Request modifyRequest(Request request);

    boolean isNotAuthNeed(Request request);
}
