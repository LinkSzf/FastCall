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
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Objects;

public abstract class FcBaseAuthHandler implements IFcAuthHandler {


    protected abstract IFcAuthProvider getAuthProvider();

    @Override
    public FcRequestContext getRequestContext(Request request) {
        FcRequestContext context = request.tag(FcRequestContext.class);
        if (Objects.isNull(context)) {
            throw new FastCallException("Required request context not found");
        }
        return context;
    }

    @Override
    public String getSystem(Request request) {
        return getRequestContext(request).getSystem();
    }

    @Override
    public boolean isNotAuthNeed(Request request) {
        return getRequestContext(request).getCallType() == FcCallType.ANONYMOUS;
    }

    @Override
    public Request modifyRequest(Request request) {
        String system = getSystem(request);
        ICredential credential = getCredential(system);

        if (Objects.isNull(credential)) {
            return request;
        }

        Request.Builder builder = request.newBuilder();
        return doModifyRequest(builder, credential)
                .tag(ICredential.class, credential)
                .build();
    }

    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        String authStr = credential.getAuthString();
        String authorization = StrUtil.prependIfMissingIgnoreCase(authStr, getAuthType().getPrefix());
        return builder.header(FcHttpHeader.AUTHORIZATION.getName(), authorization);
    }

    protected <T extends ICredential> T getCredential(String system) {
        ICredential credential = getAuthProvider().getCredential(system);
        if (Objects.isNull(credential)) {
            return null;
        }
        return (T) credential;
    }

    protected final String concatAuthString(ICredential credential) {
        String authString = credential.getAuthString();
        return StrUtil.prependIfMissingIgnoreCase(authString, getAuthType().getPrefix());
    }


}
