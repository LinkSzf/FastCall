package priv.szf.fastcall.core.auth.handler;

import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import okhttp3.Request;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Objects;
import java.util.Optional;

/**
 * Base auth handler that reads the credential from the source pak held in the context.
 * Prepends the auth type prefix, sets {@code Authorization}, and passes the request through without credential.
 */
public abstract class FcBaseAuthHandler implements IFcAuthHandler {

    @Override
    public Request modifyRequest(Request request, FcRequestContext context) {
        ICredential credential = getCredential(context);
        if (Objects.isNull(credential)) {
            return request;
        }

        Request.Builder builder = request.newBuilder();
        return doModifyRequest(builder, credential)
                .build();
    }

    protected Request.Builder doModifyRequest(@NonNull Request.Builder builder, @NonNull ICredential credential) {
        String authStr = credential.getAuthString();
        String authorization = StrUtil.prependIfMissingIgnoreCase(authStr, getAuthType().getPrefix());
        return builder.header(FcHttpHeader.AUTHORIZATION.getName(), authorization);
    }

    protected ICredential getCredential(FcRequestContext context) {
        return Optional.of(context)
                .map(FcRequestContext::getSource)
                .map(FcSourcePak::getCredential)
                .orElse(null);
    }

    protected final String concatAuthString(ICredential credential) {
        String authString = credential.getAuthString();
        return StrUtil.prependIfMissingIgnoreCase(authString, getAuthType().getPrefix());
    }


}
