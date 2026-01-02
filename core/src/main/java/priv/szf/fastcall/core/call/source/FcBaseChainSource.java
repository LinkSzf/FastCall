package priv.szf.fastcall.core.call.source;

import priv.szf.fastcall.core.model.auth.credential.ICredential;

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
                .orElse(null);
    }

    @Override
    public void updateCredential(String systemCode, ICredential credential) {
        tryUpdateCredential(systemCode, credential);
        getNextSource().updateCredential(systemCode, credential);
    }


}
