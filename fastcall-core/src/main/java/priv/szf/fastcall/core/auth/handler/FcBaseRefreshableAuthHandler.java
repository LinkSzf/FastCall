package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class FcBaseRefreshableAuthHandler extends FcBaseAuthHandler
        implements IFcRefreshableAuthHandler {

    private static final Map<String, ReentrantLock> SYSTEM_LOCKS = new ConcurrentHashMap<>();

    @Override
    protected abstract IFcDynAuthProvider getAuthProvider();

    @Override
    public final Integer getUnauthorizedCode(Request request) {
        String system = getSystem(request);
        return getAuthProvider().getUnauthorizedCode(system);
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

    public static void clearSystemLock(String system) {
        if (system != null) {
            SYSTEM_LOCKS.remove(system);
        }
    }

    private boolean doRefreshToken(Request request, Response response) {
        String system = getSystem(request);

        ReentrantLock systemLock = SYSTEM_LOCKS.computeIfAbsent(system, k -> new ReentrantLock());

        if (isCredentialInvalid(system)) {
            systemLock.lock();
            try {
                if (isCredentialInvalid(system)) {
                    getAuthProvider().refreshCredential(request, response, system);
                    return true;
                }
            } finally {
                systemLock.unlock();
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
