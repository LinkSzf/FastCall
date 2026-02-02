package priv.szf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import priv.szf.fastcall.common.FastCallConts;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.core.source.FcDatabaseSource;
import priv.szf.fastcall.core.source.FcInMemoryCacheSource;
import priv.szf.fastcall.core.source.FcRedisCacheSource;

@Configuration
public class FcSourceConfig {

    private static final String REDIS_CACHE_SOURCE = FastCallConts.NAME + "RedisCacheSource";

    private static final String IN_MEMORY_CACHE_SOURCE = FastCallConts.NAME + "InMemoryCacheSource";

    private static final String DATABASE_SOURCE = FastCallConts.NAME + "DatabaseSource";

    @ConditionalOnExpression(
            "'${fast-call.source-cache.enable:true}' == 'true' and " +
                    "'${fast-call.source-cache.engine:redis}' == 'redis'"
    )
    @ConditionalOnBean(RedisTemplate.class)
    @Bean(REDIS_CACHE_SOURCE)
    public FcRedisCacheSource fcRedisCacheSource(
            FastCallProperties properties,
            RedisTemplate<Object,Object> redisTemplate
    ) {
        return new FcRedisCacheSource(redisTemplate, properties);
    }

    @ConditionalOnProperty(
            prefix = "fast-call.source-cache",
            name = "enable",
            havingValue = "true",
            matchIfMissing = true
    )
    @ConditionalOnMissingBean(FcRedisCacheSource.class)
    @Bean(IN_MEMORY_CACHE_SOURCE)
    public FcInMemoryCacheSource fcInMemoryCacheSource() {
        return new FcInMemoryCacheSource();
    }

    @ConditionalOnBean(IFcPakProvider.class)
    @Bean(DATABASE_SOURCE)
    public FcDatabaseSource fcDatabaseSource(IFcPakProvider pakProvider) {
         return new FcDatabaseSource(pakProvider);
    }


}
