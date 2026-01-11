package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import okhttp3.Request;
import okhttp3.Response;
import priv.szf.fastcall.core.auth.IFcDynAuthProvider;
import priv.szf.fastcall.common.model.content.BaseDynAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FcBaseDynAuthProvider<C extends BaseDynAuthContent>
        extends FcBaseAuthProvider<C>
        implements IFcDynAuthProvider<C>
{

    private static final Map<String, Object> SYSTEM_LOCKS = new ConcurrentHashMap<>();

    protected abstract ICredential buildCredential(@NonNull C authContent,
                                                   @NonNull Request request,
                                                            Response response,
                                                   @NonNull String system);

    @Override
    protected ICredential buildCredential(@NonNull C authContent) {
        return null;
    }

    @Override
    public void refreshCredential(Request request, Response response, String system) {
        Object systemLock = SYSTEM_LOCKS.computeIfAbsent(system, k -> new Object());

        if (isCredentialInvalid(system)) {
            synchronized (systemLock) {
                if (isCredentialInvalid(system)) {
                    doRefreshCredential(request, response, system);
                }
            }
        }
    }

    private boolean isCredentialInvalid(String system) {
        ICredential credential = getCredential(system);
        return Objects.isNull(credential)
                || credential.isInvalid();
    }

    private void doRefreshCredential(Request request, Response response, String system) {
        ICredential credential = buildCredential(getAuthContent(system), request, response, system);
        getSource().updateCredential(system, credential);
    }



}
