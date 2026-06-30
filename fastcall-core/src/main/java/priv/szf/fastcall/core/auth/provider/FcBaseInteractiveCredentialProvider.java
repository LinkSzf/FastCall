package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.content.BaseDynAuthContent;
import priv.szf.fastcall.common.model.content.FcAuthProp;
import priv.szf.fastcall.core.FastCallClient;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcDynCredentialProvider;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Optional;

public abstract class FcBaseInteractiveCredentialProvider<C extends BaseDynAuthContent, R>
        extends FcBaseCredentialProvider<C>
        implements IFcDynCredentialProvider {

    protected abstract ICredential buildCredential(@NonNull FastCallResponse<R> response, @NonNull C authContent);

    @Override
    protected final ICredential buildCredential(@NonNull C authContent, @NonNull FcRequestContext context) {
        FcAuthPak authPak = Optional.of(context).map(FcRequestContext::getSource).map(FcSourcePak::getAuth).orElseGet(() -> FcAuthPak.builder().build());
        FcAuthProp authProp = Optional.ofNullable(authContent.getProp()).orElse(new FcAuthProp());
        FastCallClient client = resolveRefreshClient(context);
        FastCallResponse<R> authResponse = client.<R>newCall()
                .isAuth(true)
                .host(authPak.getParticularHost())
                .uri(authPak.getPath())
                .params(authProp.getParams())
                .method(FcRequestMethod.POST)
                .header(FcHttpHeader.CONTENT_TYPE, FcMediaType.APPLICATION_JSON)
                .header(FcHttpHeader.ACCEPT, FcMediaType.APPLICATION_JSON)
                .headers(authProp.getHeaders())
                .body(authProp.getBody())
                .prepared()
                .anonymousCallIt();

        checkSuccess(authResponse);

        return buildCredential(authResponse, authContent);
    }

    @Override
    protected final ICredential buildCredential(@NotNull C authContent) {
        return null;
    }

    protected final FastCallClient resolveRefreshClient(FcRequestContext context) {
        return Optional.of(context).map(FcRequestContext::getClient)
                .orElseGet(() -> FastCallClientFactory.getExistedClient(context.getSystem()));
    }

    protected final void checkSuccess(FastCallResponse<R> response) {
        if (!response.isSuccessful()) {
            throw new FastCallException("Failed to obtain authentication, code[{}], message[{}], trace-id[{}]",
                    response.getCode(),
                    response.getMessage(),
                    getTraceId(response)
            );
        }
    }

    private static String getTraceId(FastCallResponse<?> response) {
        String traceId = response.getSingleHeader("X-Request-Id");
        if (traceId == null) {
            traceId = response.getSingleHeader("X-Correlation-Id");
        }
        if (traceId == null) {
            traceId = response.getSingleHeader("Trace-Id");
        }
        return traceId;
    }
}
