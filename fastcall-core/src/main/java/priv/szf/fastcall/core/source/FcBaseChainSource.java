package priv.szf.fastcall.core.source;

import lombok.extern.slf4j.Slf4j;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Objects;
import java.util.Optional;

/**
 * Base node of the source chain that resolves the pak itself and otherwise delegates to the next source.
 * A pak obtained from the next source is passed to {@code doAfterGetFromNextSource}, e.g. for caching.
 */
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
    public FcSourcePak getSourcePak(String system) {
        FcSourcePak sourcePak = tryGetSourcePak(system);
        if (Objects.isNull(sourcePak)) {
            sourcePak = Optional.ofNullable(this.nextSource)
                    .map(source -> source.getSourcePak(system))
                    .orElse(null);
            if (Objects.nonNull(sourcePak)) {
                doAfterGetFromNextSource(system, sourcePak);
            }
        }
        return sourcePak;
    }

    @Override
    public void updateCredential(String system, ICredential credential) {
        tryUpdateCredential(system, credential);
        Optional.ofNullable(this.nextSource)
                .ifPresent(s -> s.updateCredential(system, credential));
    }

    protected void doAfterGetFromNextSource(String system, FcSourcePak sourcePak) {
    }


}

