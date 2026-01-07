package priv.szf.fastcall.core.source;

import priv.szf.fastcall.core.model.FcSourcePak;
import priv.szf.fastcall.core.model.IEssentialCheck;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.util.Objects;
import java.util.Optional;

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

        return Optional.ofNullable(getNextSource())
                .map(s -> s.getSourcePak(system))
                .map(IEssentialCheck::check)
                .orElse(null);
    }

    @Override
    public void updateCredential(String systemCode, ICredential credential) {
        tryUpdateCredential(systemCode, credential);
        Optional.ofNullable(getNextSource())
                .ifPresent(s -> s.updateCredential(systemCode, credential));
    }


}
