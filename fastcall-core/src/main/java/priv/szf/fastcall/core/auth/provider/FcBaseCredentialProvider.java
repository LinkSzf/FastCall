package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.util.ClassUtil;
import lombok.NonNull;
import priv.szf.fastcall.core.auth.FcRequestContext;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

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
