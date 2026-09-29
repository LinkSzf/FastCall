package io.github.linkszf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.core.filter.FcFilter;
import io.github.linkszf.fastcall.common.source.IFcPakProvider;
import io.github.linkszf.fastcall.core.filter.FcFilterManager;
import io.github.linkszf.fastcall.core.filter.FcRateLimitFilter;
import io.github.linkszf.fastcall.core.filter.FcRequestEventFilter;
import io.github.linkszf.fastcall.core.event.IFcRequestEventPublisher;
import io.github.linkszf.fastcall.core.filter.FcRetryFilter;
import io.github.linkszf.fastcall.core.filter.support.FcRateLimitSupport;

import java.util.List;

@Configuration
public class FcFilterConfig {

    private static final String REQUEST_EVENT_FILTER = FastCallConsts.NAME + "RequestEventFilter";

    private static final String RATE_LIMIT_FILTER = FastCallConsts.NAME + "RateLimitFilter";

    private static final String RETRY_FILTER = FastCallConsts.NAME + "RetryFilter";

    @Bean
    public FcFilterManager fcfilterManager(List<FcFilter> filters) {
        return new FcFilterManager(filters);
    }

    @ConditionalOnProperty(prefix = "fast-call.filter", name = "enable-rate-limit", havingValue = "true")
    @Bean
    public FcRateLimitSupport fcRateLimitSupport(IFcPakProvider pakProvider, FastCallProperties properties) {
        return new FcRateLimitSupport(pakProvider, properties);
    }


    @ConditionalOnBean(FcRateLimitSupport.class)
    @ConditionalOnMissingBean(name = RATE_LIMIT_FILTER)
    @Bean(RATE_LIMIT_FILTER)
    public FcRateLimitFilter fcRateLimitFilter(FcRateLimitSupport rateLimitSupport) {
        return new FcRateLimitFilter(rateLimitSupport);
    }

    @ConditionalOnBean(IFcRequestEventPublisher.class)
    @ConditionalOnMissingBean(name = REQUEST_EVENT_FILTER)
    @Bean(REQUEST_EVENT_FILTER)
    public FcRequestEventFilter fcRequestEventFilter(IFcRequestEventPublisher eventPublisher) {
        return new FcRequestEventFilter(eventPublisher);
    }

    @ConditionalOnProperty(prefix = "fast-call.filter", name = "enable-retry", havingValue = "true")
    @ConditionalOnMissingBean(name = RETRY_FILTER)
    @Bean(RETRY_FILTER)
    public FcRetryFilter fcRetryFilter() {
        return new FcRetryFilter();
    }



}
