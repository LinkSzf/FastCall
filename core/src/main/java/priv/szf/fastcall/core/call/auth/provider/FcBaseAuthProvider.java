package priv.szf.fastcall.core.call.auth.provider;

import cn.hutool.core.util.TypeUtil;
import lombok.NonNull;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class FcBaseAuthProvider<C extends BaseAuthContent> implements IFcAuthProvider<C> {

    private final Class<C> contentClazz = (Class<C>) TypeUtil.getTypeArgument(this.getClass());

    protected abstract IFcSource getSource();

    protected abstract ICredential buildCredential(@NonNull C authContent);

    @Override
    public ICredential getCredential(String system) {
        FcSourcePak sourcePak = getSource().getSourcePak(system);
        if (Objects.isNull(sourcePak)) {
            throw new FcUnexpectedException(String.format("FastCall-系统[%s]未在配置中找到", system));
        }
        ICredential credential = sourcePak.getCredential();
        return (Objects.nonNull(credential)) ? credential
                : buildCredential(system);
    }

    protected C getAuthContent(String system) {
        BaseAuthContent content = Optional.ofNullable(getSource().getSourcePak(system))
                .map(FcSourcePak::getAuth)
                .map(FcAuthPak::getContent)
                .orElseThrow(() -> new FcUnexpectedException("FastCall-未获取到认证信息"));

        return contentClazz.cast(content);
    }

    private ICredential buildCredential(String system) {
        C authContent = getAuthContent(system);
        ICredential credential = buildCredential(authContent);
        getSource().updateCredential(system, credential);
        return credential;
    }

}
