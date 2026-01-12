package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;

public abstract class FcBaseRefreshableAuthHandler extends FcBaseAuthHandler
        implements IFcRefreshableAuthHandler {

    protected abstract IFcDynAuthProvider<?> getInteractiveAuthProvider();

    @Override
    protected IFcAuthProvider<?> getAuthProvider() {
        return getInteractiveAuthProvider();
    }

    @Override
    public Integer getAuthInNeedCode(Request request) {
        String system = getSystem(request);
        return getInteractiveAuthProvider().getAuthInNeedCode(system);
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
