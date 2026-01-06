package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.credential.ICredential;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ConditionalOnExpression(
        "'${fast-call.source-cache.enable:true}' == 'true' and " +
                "'${fast-call.source-cache.engine:redis}' == 'redis'"
)
@ConditionalOnBean(RedisTemplate.class)
@Component
@RequiredArgsConstructor
public class FcRedisCacheSource extends FcBaseChainSource implements IFcSource, IFcCacheSource {

    private static final String REDIS_KEY_SOURCE_PREFIX = "fastcall:source:";

    private final RedisTemplate<Object, Object> redisTemplate;

    private final FastCallProperties properties;

    @PostConstruct
    public void init2() {
        System.out.println("初始化redis内存缓存源");
    }

    @Override
    protected FcSourcePak tryGetSourcePak(String system) {
        String redisKey = buildRedisKey(system);
        FcSourcePak sourcePak = (FcSourcePak) this.redisTemplate.opsForValue().get(redisKey);
        if (Objects.isNull(sourcePak)) {
            sourcePak = getNextSource().getSourcePak(system);
            if (Objects.nonNull(sourcePak)) {
                this.redisTemplate.opsForValue().set(redisKey, sourcePak, getExpire(), TimeUnit.MINUTES);
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
