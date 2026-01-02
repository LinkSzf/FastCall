package priv.szf.fastcall.core.call.auth;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

public interface IFcInteractiveAuthProvider<C extends BaseAuthContent> extends IFcAuthProvider<C> {

    void refreshCredential(Request request, Response response, String system);
}
