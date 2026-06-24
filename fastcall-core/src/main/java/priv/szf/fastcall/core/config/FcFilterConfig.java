package priv.szf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.core.filter.FcFilterManager;
import priv.szf.fastcall.core.filter.FcRateLimitFilter;
import priv.szf.fastcall.core.filter.support.FcRateLimitSupport;

import java.util.List;

@Configuration
public class FcFilterConfig {

    @Bean
    public FcFilterManager fcfilterManager(List<FcFilter> filters) {
        return new FcFilterManager(filters);
    }

    @ConditionalOnProperty(prefix = "fast-call", name = "enable-rate-limit", havingValue = "true", matchIfMissing = true)
    @Bean
    public FcRateLimitSupport fcRateLimitSupport(IFcPakProvider pakProvider, FastCallProperties properties) {
        FcRateLimitSupport rateLimitSupport = new FcRateLimitSupport(pakProvider, properties);
        rateLimitSupport.init();
        return rateLimitSupport;
    }


    @ConditionalOnBean(FcRateLimitSupport.class)
    @ConditionalOnMissingBean(name = "fcRateLimitFilter")
    @Bean
    public FcRateLimitFilter fcRateLimitFilter(FcRateLimitSupport rateLimitSupport) {
        return new FcRateLimitFilter(rateLimitSupport);
    }



}
