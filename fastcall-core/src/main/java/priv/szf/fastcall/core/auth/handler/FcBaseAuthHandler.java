package priv.szf.fastcall.core.auth.handler;

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import okhttp3.Request;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.util.Objects;

public abstract class FcBaseAuthHandler implements IFcAuthHandler {


    protected abstract IFcAuthProvider<?> getAuthProvider();

    @Override
    public String getSystem(Request request) {
        return getRequestContext(request).getSystem();
    }

    @Override
    public boolean isNotAuthNeed(Request request) {
        return FcCallType.ANONYMOUS == getRequestContext(request).getCallType();
    }

    @Override
    public Request modifyRequest(Request request) {
        String systemCode = getSystem(request);
        ICredential credential = getCredential(systemCode);

        if (Objects.isNull(credential)) {
            return request;
        }

        return doModifyRequest(request, credential);
    }

    protected Request doModifyRequest(@NonNull Request request, @NonNull ICredential credential) {
        String authStr = credential.getAuthString();
        String authorization = StrUtil.prependIfMissingIgnoreCase(authStr, getAuthType().getPrefix());
        return request.newBuilder()
                .tag(ICredential.class, credential)
                .header(FcHttpHeader.AUTHORIZATION.getName(), authorization)
                .build();
    }

    protected <T extends ICredential> T getCredential(String system) {
        ICredential credential = getAuthProvider().getCredential(system);
        if (Objects.isNull(credential)) {
            return null;
        }
        return (T) credential;
    }

    protected FcRequestContext getRequestContext(Request request) {
        FcRequestContext context = request.tag(FcRequestContext.class);
        if (Objects.isNull(context)) {
            throw new FastCallException("url[%s]未传递请求信息上下文", request.url());
        }
        return context;
    }


}
