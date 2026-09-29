package io.github.linkszf.fastcall.core.config;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import io.github.linkszf.fastcall.common.source.IFcCacheSource;
import io.github.linkszf.fastcall.common.source.IFcChainSource;
import io.github.linkszf.fastcall.common.source.IFcDatabaseSource;
import io.github.linkszf.fastcall.common.source.IFcPakProvider;
import io.github.linkszf.fastcall.core.source.FcDatabaseSource;
import io.github.linkszf.fastcall.core.source.FcInMemoryCacheSource;
import io.github.linkszf.fastcall.core.source.FcPropertySource;
import io.github.linkszf.fastcall.core.source.FcRedisCacheSource;
import io.github.linkszf.fastcall.core.source.FcSourceDelegate;

import java.util.List;

@Configuration
public class FcSourceConfig {

    @Bean(initMethod = "init")
    public FcSourceDelegate fcSourceDelegate(List<IFcChainSource> chainSources) {
        return new FcSourceDelegate(chainSources);
    }

    @ConditionalOnExpression(
            "'${fast-call.source-cache.enable:true}' == 'true' and " +
                    "'${fast-call.source-cache.engine:memory}' == 'redis'"
    )
    @ConditionalOnBean(RedisTemplate.class)
    @Bean
    public FcRedisCacheSource fcRedisCacheSource(
            FastCallProperties properties,
            RedisTemplate<Object,Object> redisTemplate
    ) {
        FastCallProperties.SourceCache sourceCache = properties.getSourceCache();
        return new FcRedisCacheSource(redisTemplate, sourceCache);
    }

    @ConditionalOnProperty(
            prefix = "fast-call.source-cache",
            name = "enable",
            havingValue = "true",
            matchIfMissing = true
    )
    @ConditionalOnMissingBean(IFcCacheSource.class)
    @Bean
    public FcInMemoryCacheSource fcInMemoryCacheSource(FastCallProperties properties) {
        FastCallProperties.SourceCache sourceCache = properties.getSourceCache();
        return new FcInMemoryCacheSource(sourceCache);
    }

    @ConditionalOnBean(IFcPakProvider.class)
    @ConditionalOnMissingBean(IFcDatabaseSource.class)
    @Bean
    public FcDatabaseSource fcDatabaseSource(IFcPakProvider pakProvider) {
         return new FcDatabaseSource(pakProvider);
    }

    @Bean
    public FcPropertySource fcPropertySource(FastCallProperties properties) {
        List<FastCallProperties.EasySource> sources = properties.getEasySource();
        if (CollectionUtil.isEmpty(sources)) {
            return null;
        }
        return new FcPropertySource(sources);
    }


}
