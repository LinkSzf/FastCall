package priv.szf.fastcall.core.call.auth;

import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

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
        BaseAuthContent authContent = request.tag(BaseAuthContent.class);
        if (Objects.isNull(authContent)) {
            return request;
        }
        IFcAuthHandler handler = handlerMap.get(authContent.getType());
        if (Objects.isNull(handler)) {{
            throw new RuntimeException("未找到对应的认证处理器");
        }}

        return handler.modifyRequest(request);
    }

}
