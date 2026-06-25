package priv.szf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.core.filter.FcFilterManager;
import priv.szf.fastcall.core.filter.FcRateLimitFilter;
import priv.szf.fastcall.core.filter.FcRequestEventFilter;
import priv.szf.fastcall.core.event.IFcRequestEventPublisher;
import priv.szf.fastcall.core.filter.support.FcRateLimitSupport;

import java.util.List;

@Configuration
public class FcFilterConfig {

    private static final String REQUEST_EVENT_FILTER = FastCallConsts.NAME + "RequestEventFilter";

    private static final String RATE_LIMIT_FILTER = FastCallConsts.NAME + "RateLimitFilter";

    @Bean
    public FcFilterManager fcfilterManager(List<FcFilter> filters) {
        return new FcFilterManager(filters);
    }

    @ConditionalOnProperty(prefix = "fast-call.filter", name = "enable-rate-limit", havingValue = "true")
    @Bean
    public FcRateLimitSupport fcRateLimitSupport(IFcPakProvider pakProvider, FastCallProperties properties) {
        FcRateLimitSupport rateLimitSupport = new FcRateLimitSupport(pakProvider, properties);
        rateLimitSupport.init();
        return rateLimitSupport;
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



}
