package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.config.FastCallProperties;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Order(100)
@RequiredArgsConstructor
public class FcInMemoryCacheSource extends FcBaseChainSource implements IFcCacheSource, IFcSource {

    private final FastCallProperties.SourceCache sourceCache;

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
        if (Objects.nonNull(sourcePak)) {
            sourcePak.setCredential(credential);
        }
    }

    @Override
    public void invalidate(String system) {
        if (Objects.nonNull(system)) {
            this.cache.remove(system);
        }
    }
}
