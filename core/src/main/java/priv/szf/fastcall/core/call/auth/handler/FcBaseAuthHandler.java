package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import priv.szf.fastcall.core.call.auth.IFcAuthHandler;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.util.Objects;
import java.util.Optional;

public abstract class FcBaseAuthHandler<T extends BaseAuthContent> implements IFcAuthHandler {

    T getAuthContent(Request request) {
        FcAuthPak authPak = request.tag(FcAuthPak.class);
        if (Objects.isNull(authPak)) {
            throw new FcUnexpectedException(String.format("请求[%s]未获取到设置的认证信息", request.url()));
        }
        return (T) Optional.ofNullable(authPak)
                .map(FcAuthPak::getContent)
                .orElseThrow(()
                        -> new FcUnexpectedException(
                                String.format("请求[%s]未获取到设置的认证信息", request.url())));
    }

    String getSystemCode(Request request) {
        FcSystemPak system = request.tag(FcSystemPak.class);
        if (Objects.isNull(system)) {
            throw new FcUnexpectedException(String.format("请求[%s]未获取到相关系统信息", request.url()));
        }
        return system.getCode();
    }


}
