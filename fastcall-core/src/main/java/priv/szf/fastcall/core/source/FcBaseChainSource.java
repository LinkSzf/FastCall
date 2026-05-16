package priv.szf.fastcall.core.source;

import lombok.extern.slf4j.Slf4j;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.IEssentialCheck;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class FcBaseChainSource implements IFcChainSource {

    private IFcSource nextSource;

    protected abstract FcSourcePak tryGetSourcePak(String system);

    protected abstract void tryUpdateCredential(String system, ICredential credential);

    @Override
    public void setNextSource(IFcSource nextSource) {
        this.nextSource = nextSource;
    }

    @Override
    public IFcSource getNextSource() {
        return this.nextSource;
    }

    @Override
    public FcSourcePak getSourcePak(String system) {
        FcSourcePak sourcePak = tryGetSourcePak(system);
        if (Objects.nonNull(sourcePak)) {
            return sourcePak;
        }

        log.debug("{}-source pak is not found in {}: system[{}]", FastCallConsts.NAME, this.getClass().getSimpleName(), system);

        return Optional.ofNullable(getNextSource())
                .map(s -> s.getSourcePak(system))
                .map(IEssentialCheck::check)
                .orElse(null);
    }

    @Override
    public void updateCredential(String system, ICredential credential) {
        tryUpdateCredential(system, credential);
        Optional.ofNullable(getNextSource())
                .ifPresent(s -> s.updateCredential(system, credential));
    }


}

