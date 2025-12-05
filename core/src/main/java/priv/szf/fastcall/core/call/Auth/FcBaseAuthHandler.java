package priv.szf.fastcall.core.call.Auth;

import okhttp3.Request;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.util.Objects;

public abstract class FcBaseAuthHandler<T extends BaseAuthContent> implements IFcAuthHandler {


    T getContent(Request request) {
        BaseAuthContent authContent = request.tag(BaseAuthContent.class);
        if (Objects.isNull(authContent)) {
            throw new RuntimeException(String.format("请求[%s]未获取到设置的认证信息", request.url()));
        }
        return (T) authContent;
    }



}
