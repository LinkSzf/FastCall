package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.util.TypeUtil;
import lombok.NonNull;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class FcBaseAuthProvider<C extends BaseAuthContent> implements IFcAuthProvider {

    private final Class<C> contentClazz = (Class<C>) TypeUtil.getTypeArgument(this.getClass());

    protected abstract IFcSource getSource();

    protected abstract ICredential buildCredential(@NonNull C authContent);

    @Override
    public ICredential getCredential(String system) {
        FcSourcePak sourcePak = getSource().getSourcePak(system);
        if (Objects.isNull(sourcePak)) {
            throw new FastCallException("Source infos of system[{}] not found", system);
        }
        ICredential credential = sourcePak.getCredential();
        return (Objects.nonNull(credential)) ? credential
                : buildCredential(system);
    }

    protected C getAuthContent(String system) {
        BaseAuthContent content = Optional.ofNullable(getSource().getSourcePak(system))
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getContent)
                .orElseThrow(() -> new FastCallException("Source infos about auth-content of system[{}] not found", system));

        return contentClazz.cast(content);
    }

    private ICredential buildCredential(String system) {
        C authContent = getAuthContent(system);
        ICredential credential = buildCredential(authContent);
        getSource().updateCredential(system, credential);
        return credential;
    }

}
