package priv.szf.fastcall.core.auth.handler;

import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcDynCredentialProvider;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class FcBaseRefreshableAuthHandler extends FcBaseAuthHandler
        implements IFcRefreshableAuthHandler {

    private static final Map<String, ReentrantLock> SYSTEM_LOCKS = new ConcurrentHashMap<>();

    @Override
    protected abstract IFcDynCredentialProvider getCredentialProvider();

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
        FcRequestContext context = getRequestContext(request);
        String system = context.getSystem();
        ReentrantLock systemLock = SYSTEM_LOCKS.computeIfAbsent(system, k -> new ReentrantLock());

        if (isCredentialInvalid(request)) {
            systemLock.lock();
            try {
                if (isCredentialInvalid(request)) {
                    getCredentialProvider().buildCredential(context);
                    return true;
                }
            } finally {
                systemLock.unlock();
            }
        }

        return false;
    }

    private boolean isCredentialInvalid(Request request) {
        return Optional.of(request)
                .map(this::getRequestContext)
                .map(FcRequestContext::getSource)
                .map(FcSourcePak::getCredential)
                .map(ICredential::isInvalid)
                .orElse(true);
    }

}
