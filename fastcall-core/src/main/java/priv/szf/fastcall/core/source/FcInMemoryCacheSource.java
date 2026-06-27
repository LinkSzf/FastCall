package priv.szf.fastcall.core.source;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.config.FastCallProperties;

import java.util.Objects;


@Order(100)
@RequiredArgsConstructor
public class FcInMemoryCacheSource extends FcBaseChainSource implements IFcCacheSource, IFcSource {

    private final FastCallProperties.SourceCache sourceCache;

    private Cache<String, FcSourcePak> cache;

    @Override
    public void init() {
        boolean indefinite = this.sourceCache.isIndefinite();
        long expire = (indefinite) ? 0L : this.sourceCache.getExpire();
        long expireMillis = expire * 1000L;
        this.cache = new TimedCache<>(expireMillis);
    }

    @Override
    protected FcSourcePak tryGetSourcePak(String system) {
        return this.cache.get(system);
    }

    @Override
    protected void doAfterGetFromNextSource(String system, FcSourcePak sourcePak) {
        if (Objects.isNull(sourcePak)) {
            this.cache.remove(system);
        } else {
            this.cache.put(system, sourcePak);
        }
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
