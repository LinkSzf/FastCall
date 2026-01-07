package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.FcSourcePak;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class FcRedisCacheSource extends FcBaseChainSource implements IFcSource, IFcCacheSource {

    private static final String REDIS_KEY_SOURCE_PREFIX = "fastcall:source:";

    private final RedisTemplate<Object, Object> redisTemplate;

    private final FastCallProperties properties;

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
    public int getWeight() {
        return 3;
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
        return this.properties.getSourceCache().getExpire();
    }
}
