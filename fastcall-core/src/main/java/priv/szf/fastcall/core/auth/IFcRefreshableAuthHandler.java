package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import okhttp3.Response;

public interface IFcRefreshableAuthHandler extends IFcAuthHandler {

    boolean preRefresh(Request request);

    boolean refresh(Response response);

    Integer getUnauthorizedCode(Request request);
}
