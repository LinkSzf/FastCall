package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.core.model.FcSourcePak;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@RequiredArgsConstructor
public class FcInMemoryCacheSource extends FcBaseChainSource implements IFcSource, IFcCacheSource {

    private final Map<String, FcSourcePak> cache = new ConcurrentHashMap<>();

    @Override
    protected FcSourcePak tryGetSourcePak(String system) {
        FcSourcePak sourcePak = this.cache.computeIfAbsent(system, getNextSource()::getSourcePak);
        if (Objects.isNull(sourcePak)) {
            this.cache.remove(system);
        }
        return sourcePak;
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
        FcSourcePak sourcePak = getSourcePak(system);
        sourcePak.setCredential(credential);
    }

    @Override
    public int getWeight() {
        return 2;
    }

    @Override
    public void invalidate(String system) {
        if (Objects.nonNull(system)) {
            this.cache.remove(system);
        }
    }
}
