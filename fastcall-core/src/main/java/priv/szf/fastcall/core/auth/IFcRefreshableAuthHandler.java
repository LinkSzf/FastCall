package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import okhttp3.Response;

public interface IFcRefreshableAuthHandler extends IFcAuthHandler {

    void preRefresh(Request request);

    void refresh(Response response);

    Integer getAuthInNeedCode(Request request);
}
