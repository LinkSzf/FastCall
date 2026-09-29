package io.github.linkszf.fastcall.core.auth.provider;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import io.github.linkszf.fastcall.common.FcHttpHeader;
import io.github.linkszf.fastcall.common.FcMediaType;
import io.github.linkszf.fastcall.common.FcRequestMethod;
import io.github.linkszf.fastcall.common.exception.FastCallException;
import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.common.model.content.BaseDynAuthContent;
import io.github.linkszf.fastcall.common.model.content.FcAuthProp;
import io.github.linkszf.fastcall.core.FastCallClient;
import io.github.linkszf.fastcall.core.FastCallClientFactory;
import io.github.linkszf.fastcall.core.FastCallResponse;
import io.github.linkszf.fastcall.core.auth.FcRequestContext;
import io.github.linkszf.fastcall.core.auth.IFcDynCredentialProvider;
import io.github.linkszf.fastcall.common.model.FcAuthPak;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

import java.util.Optional;

/**
 * Base provider for interactive auth: it calls the configured auth endpoint anonymously and builds the credential
 * from that response; a non-successful auth response raises a {@code FastCallException}.
 */
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
