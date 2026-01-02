package priv.szf.fastcall.core.call.auth.handler;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.FcRequestContext;
import priv.szf.fastcall.core.call.auth.IFcAuthHandler;
import priv.szf.fastcall.core.call.auth.IFcRefreshableAuthHandler;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FcAuthHandlerDelegate implements IFcAuthHandler {

    private final Map<AuthType, IFcAuthHandler> handlerMap;

    @Autowired
    public FcAuthHandlerDelegate(List<IFcAuthHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(IFcAuthHandler::getAuthType, Function.identity()));
    }

    @Override
    public AuthType getAuthType() {
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

    public boolean isAuthRefreshable(Request request) {
        IFcAuthHandler handler = getHandler(request);
        return isRefreshableHandler(handler);
    }

    public boolean preRefresh(Request request) {
        IFcAuthHandler handler = getHandler(request);

        if (!isRefreshableHandler(handler)) {
            return false;
        }

        ((IFcRefreshableAuthHandler) handler).preRefresh(request);
        return true;
    }

    public boolean refresh(Response response) {
        int code = response.code();
        if (code != HttpStatus.UNAUTHORIZED.value()) {
            return false;
        }

        Request request = response.request();
        doExtraForRequest(request);

        IFcAuthHandler handler = getHandler(request);
        if (!isRefreshableHandler(handler)) {
            return false;
        }

        ((IFcRefreshableAuthHandler) handler).refresh(response);
        return true;
    }

    protected void doExtraForRequest(Request request) {
        ICredential credential = request.tag(ICredential.class);
        if (Objects.nonNull(credential)) {
            credential.invalidate();
        }
    }

    private IFcAuthHandler getHandler(Request request) {
        AuthType authType = Optional.ofNullable(request.tag(FcRequestContext.class))
                .map(FcRequestContext::getAuthType)
                .orElseThrow(() -> new FcUnexpectedException(String.format("FastCall-url[%s]未传递认证类型上下文", request.url())));

        IFcAuthHandler handler = handlerMap.get(authType);
        if (Objects.isNull(handler)) {{
            throw new FcUnexpectedException(String.format("FastCall-url[%s]未找到对应[%s]的认证处理器", request.url(), authType));
        }}

        return handler;
    }

    private boolean isRefreshableHandler(IFcAuthHandler handler) {
        return handler instanceof IFcRefreshableAuthHandler;
    }
}
