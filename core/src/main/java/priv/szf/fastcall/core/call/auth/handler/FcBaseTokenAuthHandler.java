package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.auth.IFcInteractiveAuthProvider;
import priv.szf.fastcall.core.call.auth.IFcRefreshableAuthHandler;

public abstract class FcBaseTokenAuthHandler extends FcBaseAuthHandler
        implements IFcRefreshableAuthHandler {

    protected abstract IFcInteractiveAuthProvider<?> getInteractiveAuthProvider();

    @Override
    protected IFcAuthProvider<?> getAuthProvider() {
        return getInteractiveAuthProvider();
    }

    @Override
    public void preRefresh(Request request) {
        doRefreshToken(request, null);
    }

    @Override
    public void refresh(Response response) {
        Request request = response.request();
        doRefreshToken(request, response);
    }

    protected void doRefreshToken(Request request, Response response) {
        String system = getSystem(request);
        getInteractiveAuthProvider().refreshCredential(request, response, system);
    }

}
