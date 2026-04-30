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
import java.util.concurrent.locks.ReentrantLock;

public abstract class FcBaseRefreshableAuthHandler extends FcBaseAuthHandler
        implements IFcRefreshableAuthHandler {

    private static final Map<String, ReentrantLock> SYSTEM_LOCKS = new ConcurrentHashMap<>();

    private static final Map<String, Boolean> LOCKS_PENDING_REMOVAL = new ConcurrentHashMap<>();

    public static void clearSystemLock(String system) {
        if (system == null) {
            return;
        }
        //为延迟清理标记锁。不立即移除以避免创建第二个锁对象，而另一个线程仍然持有旧的锁对象。
        LOCKS_PENDING_REMOVAL.put(system, Boolean.TRUE);
        tryRemoveSystemLock(system);
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

        ReentrantLock systemLock = SYSTEM_LOCKS.computeIfAbsent(system, k -> new ReentrantLock());

        if (isCredentialInvalid(system)) {
            systemLock.lock();
            try {
                if (isCredentialInvalid(system)) {
                    getInteractiveAuthProvider().refreshCredential(request, response, system);
                    return true;
                }
            } finally {
                systemLock.unlock();
                if (LOCKS_PENDING_REMOVAL.containsKey(system)) {
                    tryRemoveSystemLock(system);
                }
            }
        }

        return false;
    }

    private static void tryRemoveSystemLock(String system) {
        ReentrantLock lock = SYSTEM_LOCKS.get(system);
        if (lock == null) {
            LOCKS_PENDING_REMOVAL.remove(system);
            return;
        }
        if (!lock.isLocked()
                && !lock.hasQueuedThreads()
                && SYSTEM_LOCKS.remove(system, lock)) {
            LOCKS_PENDING_REMOVAL.remove(system);
        }
    }

    private boolean isCredentialInvalid(String system) {
        ICredential credential = getCredential(system);
        return Objects.isNull(credential)
                || credential.isInvalid();
    }

}
