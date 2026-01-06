package priv.szf.fastcall.core.auth;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.BaseAuthContent;

public interface IFcInteractiveAuthProvider<C extends BaseAuthContent> extends IFcAuthProvider<C> {

    void refreshCredential(Request request, Response response, String system);
}
