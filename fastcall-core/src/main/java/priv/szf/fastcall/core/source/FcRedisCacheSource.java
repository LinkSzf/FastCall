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


/**
 * Cache node of the source chain that stores the pak of a system in Redis with the configured TTL.
 * Registered only when the source cache is enabled with the redis engine and a {@code RedisTemplate} exists.
 */
@Order(100)
@RequiredArgsConstructor
public class FcRedisCacheSource extends FcBaseChainSource implements IFcCacheSource, IFcSource {

    private static final String REDIS_KEY_SOURCE_PREFIX = "fastcall:source:";

    private final RedisTemplate<Object, Object> redisTemplate;

    private final FastCallProperties.SourceCache sourceCache;


    @Override
    protected FcSourcePak tryGetSourcePak(String system) {
        String redisKey = buildRedisKey(system);
        return (FcSourcePak) this.redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    protected void doAfterGetFromNextSource(String system, FcSourcePak sourcePak) {
            String redisKey = buildRedisKey(system);
            if (Objects.nonNull(sourcePak)) {
                save(redisKey, sourcePak);
            } else {
                this.redisTemplate.delete(redisKey);
            }
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
        String redisKey = buildRedisKey(system);
        FcSourcePak sourcePak = (FcSourcePak) this.redisTemplate.opsForValue().get(redisKey);

        if (Objects.nonNull(sourcePak)) {
            sourcePak.setCredential(credential);
            save(redisKey, sourcePak);
        }
    }

    @Override
    public void invalidate(String system) {
        if (Objects.nonNull(system)) {
            String key = buildRedisKey(system);
            this.redisTemplate.delete(key);
        }
    }

    private void save(String redisKey, FcSourcePak sourcePak) {
        if (this.sourceCache.isIndefinite()) {
            this.redisTemplate.opsForValue().set(redisKey, sourcePak);
        } else {
            long expire = this.sourceCache.getExpire();
            this.redisTemplate.opsForValue().set(redisKey, sourcePak, expire, TimeUnit.SECONDS);
        }
    }

    private String buildRedisKey(String system) {
        return REDIS_KEY_SOURCE_PREFIX + system;
    }

}
