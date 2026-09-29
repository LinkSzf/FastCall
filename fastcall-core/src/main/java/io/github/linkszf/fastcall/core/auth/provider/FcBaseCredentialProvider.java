package io.github.linkszf.fastcall.core.auth.provider;

import cn.hutool.core.util.ClassUtil;
import lombok.NonNull;
import io.github.linkszf.fastcall.core.auth.FcRequestContext;
import io.github.linkszf.fastcall.core.auth.IFcCredentialProvider;
import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.common.exception.FastCallException;
import io.github.linkszf.fastcall.common.model.FcAuthPak;
import io.github.linkszf.fastcall.common.model.content.BaseAuthContent;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

import java.util.Optional;

/**
 * Base credential provider that resolves the typed auth content from the source pak in the context
 * and delegates to {@code buildCredential(C)}; missing auth content raises a {@code FastCallException}.
 */
@SuppressWarnings("unchecked")
public abstract class FcBaseCredentialProvider<C extends BaseAuthContent> implements IFcCredentialProvider {

    private final Class<C> contentClazz = (Class<C>) ClassUtil.getTypeArgument(this.getClass());

    protected abstract ICredential buildCredential(@NonNull C authContent);

    @Override
    public ICredential buildCredential(FcRequestContext context) {
        C authContent = getAuthContent(context);
        return buildCredential(authContent, context);
    }

    protected ICredential buildCredential(C authContent, FcRequestContext context) {
        return buildCredential(authContent);
    }

    protected final C getAuthContent(FcRequestContext context) {
        return Optional.ofNullable(context)
                .map(FcRequestContext::getSource)
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getContent)
                .map(contentClazz::cast)
                .orElseThrow(() -> new FastCallException("Source infos about auth-content not found"));

    }

}
