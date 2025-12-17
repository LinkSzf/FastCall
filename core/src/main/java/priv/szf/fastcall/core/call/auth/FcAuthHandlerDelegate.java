package priv.szf.fastcall.core.call.auth;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcAuthPak;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FcAuthHandlerDelegate implements IFcAuthHandler {

    private final Map<String, Object> systemLocks = new ConcurrentHashMap<>();

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
    public FcSourcePak getSourceInfo(Request request) {
        return getHandler(request).getSourceInfo(request);
    }

    @Override
    public String getSystemCode(Request request) {
        return getHandler(request).getSystemCode(request);
    }

    @Override
    public Request modifyRequest(Request request) {
        return getHandler(request).modifyRequest(request);
    }

    @Override
    public boolean isAuthRefreshable(Request request) {
        IFcAuthHandler handler = getHandler(request);
        return handler.isAuthRefreshable(request);
    }

    private IFcAuthHandler getHandler(Request request) {
        AuthType authType = Optional.ofNullable(request.tag(FcSourcePak.class))
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getType)
                .orElseThrow(() -> new FcUnexpectedException(String.format("FastCall-url[%s]未传递认证类型上下文", request.url())));

        IFcAuthHandler handler = handlerMap.get(authType);
        if (Objects.isNull(handler)) {{
            throw new FcUnexpectedException(String.format("FastCall-url[%s]未找到对应的认证处理器", request.url()));
        }}

        return handler;
    }

    public boolean preRefreshTokenIfNecessary(Request request) {
        IFcAuthHandler handler = getHandler(request);
        if (!handler.isAuthPreRefreshable()) {
            return false;
        }

        doRefreshToken(handler, request, null);

        return true;
    }

    public boolean refreshTokenIfNecessary(Request request, Response response) {
        int code = response.code();
        if (code != 401) {
            return false;
        }

        IFcAuthHandler handler = getHandler(request);
        doRefreshToken(handler, request, response);
        return true;
    }

    private void doRefreshToken(IFcAuthHandler handler, Request request, Response response) {
        FcSourcePak sourceInfo = handler.getSourceInfo(request);
        String systemCode = handler.getSystemCode(request);
        Object systemLock = systemLocks.computeIfAbsent(systemCode, k -> new Object());

        if (handler.isInvalidToken(sourceInfo.getAccessToken())) {
            synchronized (systemLock) {
                if (handler.isInvalidToken(sourceInfo.getAccessToken())) {
                    handler.refreshToken(request, response);
                }
            }
        }
    }
}
