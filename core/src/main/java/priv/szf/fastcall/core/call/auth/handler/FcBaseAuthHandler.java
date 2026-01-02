package priv.szf.fastcall.core.call.auth.handler;

import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.common.FcCallType;
import priv.szf.fastcall.core.call.auth.FcRequestContext;
import priv.szf.fastcall.core.call.auth.IFcAuthHandler;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.common.FcHttpHeader;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

import java.util.Objects;

public abstract class FcBaseAuthHandler implements IFcAuthHandler {


    protected abstract IFcAuthProvider<?> getAuthProvider();

    @Override
    public String getSystem(Request request) {
        String system = getRequestContext(request).getSystem();
        if (Objects.isNull(system)) {
            throw new FcUnexpectedException(String.format("FastCall-url[%s]未传递系统信息上下文", request.url()));
        }
        return system;
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

        String authStr = credential.getAuthString();
        String authorization = StringUtils.prependIfMissing(authStr, getAuthType().getPrefix());
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
            throw new FcUnexpectedException(String.format("FastCall-url[%s]未传递请求信息上下文", request.url()));
        }
        return context;
    }


}
