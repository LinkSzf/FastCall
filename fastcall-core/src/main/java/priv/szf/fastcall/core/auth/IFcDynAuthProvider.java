package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import okhttp3.Response;

public interface IFcDynAuthProvider extends IFcAuthProvider {

    void refreshCredential(Request request, Response response, String system);

    Integer getUnauthorizedCode(String system);
}
