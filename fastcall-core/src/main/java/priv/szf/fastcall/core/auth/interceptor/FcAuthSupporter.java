package priv.szf.fastcall.core.auth.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import okhttp3.Request;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.core.auth.IFcDynCredentialProvider;
import priv.szf.fastcall.core.source.FcSourceDelegate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FcAuthSupporter {

    private final Map<String, ReentrantLock> SYSTEM_LOCKS = new ConcurrentHashMap<>();

    private final Map<FcAuthType, IFcAuthHandler> authHandlerMap;

    private final Map<FcAuthType, IFcCredentialProvider> credentialProviderMap;

    private final FcSourceDelegate source;

    public FcAuthSupporter(List<IFcAuthHandler> authHandlers, List<IFcCredentialProvider> credentialProviders, FcSourceDelegate source) {
        this.authHandlerMap = (CollectionUtil.isEmpty(authHandlers)) ?
                Collections.emptyMap()
                : Collections.unmodifiableMap(
                authHandlers.stream().collect(Collectors.toMap(IFcAuthHandler::getAuthType, Function.identity()))
        );

        this.credentialProviderMap = (CollectionUtil.isEmpty(credentialProviders)) ?
                Collections.emptyMap()
                : Collections.unmodifiableMap(
                credentialProviders.stream().collect(Collectors.toMap(IFcCredentialProvider::getAuthType, Function.identity()))
        );

        this.source = source;
    }

    public Request modifyRequest(Request request, FcRequestContext context) {
        FcAuthType authType = context.getAuthType();
        return getAuthHandler(authType).modifyRequest(request);
    }

    public boolean isAuthNotRefreshable(FcAuthType authType) {
        return Optional.of(authType)
                .map(this.credentialProviderMap::get)
                .map(handler -> !(handler instanceof IFcDynCredentialProvider))
                .orElse(true);
    }

    public void clearSystemLock(String system) {
        if (system != null) {
            this.SYSTEM_LOCKS.remove(system);
        }
    }

    public boolean refresh(FcRequestContext context) {
        FcAuthType authType = context.getAuthType();
        if (isAuthNotRefreshable(authType)) {
            return false;
        }

        String system = context.getSystem();
        ReentrantLock systemLock = SYSTEM_LOCKS.computeIfAbsent(system, k -> new ReentrantLock());

        if (isCredentialInvalid(context)) {
            systemLock.lock();
            try {
                if (isCredentialInvalid(context)) {
                    ICredential credential = getCredentialProvider(authType).buildCredential(context);
                    updateCredential(context, credential);
                    return true;
                }
            } finally {
                systemLock.unlock();
            }
        }

        return false;
    }

    private boolean isCredentialInvalid(FcRequestContext context) {
        return Optional.of(context)
                .map(FcRequestContext::getSource)
                .map(FcSourcePak::getCredential)
                .map(ICredential::isInvalid)
                .orElse(true);
    }

    public void invalidateCredential(FcRequestContext context) {
        Optional.of(context)
                .map(FcRequestContext::getSource)
                .map(FcSourcePak::getCredential)
                .ifPresent(ICredential::invalidate);
    }

    private void updateCredential(FcRequestContext context, ICredential credential) {
        String system = context.getSystem();
        context.getSource().setCredential(credential);
        this.source.updateCredential(system, credential);
    }


    private IFcAuthHandler getAuthHandler(FcAuthType authType) {
        return Optional.of(authType)
                .map(this.authHandlerMap::get)
                .orElseThrow(() -> new IllegalArgumentException("No auth handler found for auth type: " + authType));
    }

    private IFcCredentialProvider getCredentialProvider(FcAuthType authType) {
        return Optional.of(authType)
                .map(this.credentialProviderMap::get)
                .orElseThrow(() -> new IllegalArgumentException("No credential provider found for auth type: " + authType));
    }

}
