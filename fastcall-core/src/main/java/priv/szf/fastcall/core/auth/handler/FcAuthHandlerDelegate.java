package priv.szf.fastcall.core.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FcAuthHandlerDelegate implements IFcAuthHandler {

    private final Map<FcAuthType, IFcAuthHandler> handlerMap;

    @Autowired
    public FcAuthHandlerDelegate(List<IFcAuthHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(IFcAuthHandler::getAuthType, Function.identity()));
    }

    @Override
    public FcAuthType getAuthType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSystem(Request request) {
        return getHandler(request).getSystem(request);
    }

    @Override
    public Request modifyRequest(Request request) {
        return getHandler(request).modifyRequest(request);
    }

    @Override
    public boolean isNotAuthNeed(Request request) {
        return getHandler(request).isNotAuthNeed(request);
    }

    @Override
    public FcRequestContext getRequestContext(Request request) {
        return getHandler(request).getRequestContext(request);
    }

    public boolean isAuthRefreshable(Request request) {
        IFcAuthHandler handler = getHandler(request);
        return isRefreshableHandler(handler);
    }

    public boolean preRefresh(Request request) {
        IFcAuthHandler handler = getHandler(request);

        if (!isRefreshableHandler(handler)) {
            return false;
        }
        IFcRefreshableAuthHandler refreshableHandler = (IFcRefreshableAuthHandler) handler;
        return refreshableHandler.preRefresh(request);
    }

    public boolean refreshIfNecessary(Request request, Response response) {
        IFcAuthHandler handler = getHandler(request);
        if (!isRefreshableHandler(handler)) {
            return false;
        }
        IFcRefreshableAuthHandler refreshableAuthHandler = (IFcRefreshableAuthHandler) handler;
        Integer statusCode = refreshableAuthHandler.getAuthInNeedCode(request);
        if (Objects.isNull(statusCode) || statusCode != response.code()) {
            return false;
        }

        invalidateCredential(request);
        return refreshableAuthHandler.refresh(response);
    }

    protected void invalidateCredential(Request request) {
        ICredential credential = request.tag(ICredential.class);
        if (Objects.nonNull(credential)) {
            credential.invalidate();
        }
    }

    private IFcAuthHandler getHandler(Request request) {
        FcAuthType authType = Optional.ofNullable(request.tag(FcRequestContext.class))
                .map(FcRequestContext::getAuthType)
                .orElseThrow(() -> new FcUnexpectedException("Auth-type not found in request context"));

        IFcAuthHandler handler = handlerMap.get(authType);
        if (Objects.isNull(handler)) {
            throw new FcUnexpectedException("No handler corresponding to auth-type[{}] found", authType);
        }

        return handler;
    }

    private boolean isRefreshableHandler(IFcAuthHandler handler) {
        return handler instanceof IFcRefreshableAuthHandler;
    }
}
