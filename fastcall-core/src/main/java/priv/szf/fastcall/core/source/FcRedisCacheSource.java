package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Order(100)
@RequiredArgsConstructor
public class FcRedisCacheSource extends FcBaseChainSource implements IFcCacheSource, IFcSource {

    private static final String REDIS_KEY_SOURCE_PREFIX = "fastcall:source:";

    private final RedisTemplate<Object, Object> redisTemplate;

    private final FastCallProperties.SourceCache sourceCache;


    @Override
    protected FcSourcePak tryGetSourcePak(String system) {
        String redisKey = buildRedisKey(system);
        FcSourcePak sourcePak = (FcSourcePak) this.redisTemplate.opsForValue().get(redisKey);
        if (Objects.isNull(sourcePak)) {
            sourcePak = getNextSource().getSourcePak(system);
            if (Objects.nonNull(sourcePak)) {
                this.redisTemplate.opsForValue().set(redisKey, sourcePak, getExpire(), TimeUnit.MINUTES);
            } else {
                this.redisTemplate.delete(redisKey);
            }
        }

        return sourcePak;
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
        String redisKey = buildRedisKey(system);
        FcSourcePak sourcePak = (FcSourcePak) this.redisTemplate.opsForValue().get(redisKey);

        if (Objects.nonNull(sourcePak)) {
            sourcePak.setCredential(credential);
            this.redisTemplate.opsForValue().set(redisKey, sourcePak, getExpire(), TimeUnit.MINUTES);
        }
    }

    @Override
    public void invalidate(String system) {
        if (Objects.nonNull(system)) {
            String key = buildRedisKey(system);
            this.redisTemplate.delete(key);
        }
    }

    private String buildRedisKey(String system) {
        return REDIS_KEY_SOURCE_PREFIX + system;
    }

    private long getExpire() {
        return this.sourceCache.getExpire();
    }
}
