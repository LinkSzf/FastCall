package priv.szf.fastcall.core.call.auth;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FcAuthHandlerDelegate {

    private final Map<AuthType, IFcAuthHandler> handlerMap;

    @Autowired
    public FcAuthHandlerDelegate(List<IFcAuthHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(IFcAuthHandler::getAuthType, Function.identity()));
    }

    public Request getNewRequest(Request request) {
        AuthType authType = request.tag(AuthType.class);
        if (Objects.isNull(authType)) {
            return request;
        }

        IFcAuthHandler handler = handlerMap.get(authType);
        if (Objects.isNull(handler)) {{
            throw new FcUnexpectedException("FastCall-未找到对应的认证处理器");
        }}

        return handler.modifyRequest(request);
    }


    public boolean isInvalidToken(FcTokenPak accessToken) {
        if (Objects.isNull(accessToken)) {
            return true;
        }

        LocalDateTime estimatedExpirationTime = accessToken.getEstimatedExpirationTime();
        return Objects.isNull(estimatedExpirationTime)
                || estimatedExpirationTime.isBefore(LocalDateTime.now());
    }
}
