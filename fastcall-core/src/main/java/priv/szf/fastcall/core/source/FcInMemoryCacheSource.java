package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.credential.ICredential;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(
        prefix = "fast-call.source-cache",
        name = "enable",
        havingValue = "true",
        matchIfMissing = true
)
@ConditionalOnMissingBean(value = FcRedisCacheSource.class)
@Component
@RequiredArgsConstructor
public class FcInMemoryCacheSource extends FcBaseChainSource implements IFcSource, IFcCacheSource {

    private final Map<String, FcSourcePak> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init2() {
        System.out.println("初始化内存缓存源");
    }

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
