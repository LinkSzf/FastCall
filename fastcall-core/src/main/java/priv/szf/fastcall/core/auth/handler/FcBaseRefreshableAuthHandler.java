package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FcBaseRefreshableAuthHandler extends FcBaseAuthHandler
        implements IFcRefreshableAuthHandler {

    private static final Map<String, Object> SYSTEM_LOCKS = new ConcurrentHashMap<>();

    public static void clearSystemLock(String system) {
        if (system != null) {
            SYSTEM_LOCKS.remove(system);
        }
    }

    protected abstract IFcDynAuthProvider<?> getInteractiveAuthProvider();

    @Override
    protected IFcAuthProvider getAuthProvider() {
        return getInteractiveAuthProvider();
    }

    @Override
    public Integer getAuthInNeedCode(Request request) {
        String system = getSystem(request);
        return getInteractiveAuthProvider().getAuthInNeedCode(system);
    }

    @Override
    public boolean preRefresh(Request request) {
        return doRefreshToken(request, null);
    }

    @Override
    public boolean refresh(Response response) {
        Request request = response.request();
        return doRefreshToken(request, response);
    }

    protected boolean doRefreshToken(Request request, Response response) {
        String system = getSystem(request);

        Object systemLock = SYSTEM_LOCKS.computeIfAbsent(system, k -> new Object());

        if (isCredentialInvalid(system)) {
            synchronized (systemLock) {
                if (isCredentialInvalid(system)) {
                    getInteractiveAuthProvider().refreshCredential(request, response, system);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isCredentialInvalid(String system) {
        ICredential credential = getCredential(system);
        return Objects.isNull(credential)
                || credential.isInvalid();
    }

}
