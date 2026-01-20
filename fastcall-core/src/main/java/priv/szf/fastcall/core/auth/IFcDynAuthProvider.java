package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.content.BaseAuthContent;

public interface IFcDynAuthProvider<C extends BaseAuthContent> extends IFcAuthProvider {

    void refreshCredential(Request request, Response response, String system);

    Integer getAuthInNeedCode(String request);
}
