package priv.szf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.core.event.FcSourceEventListener;
import priv.szf.fastcall.core.event.IFcSourceEventListener;

import java.util.List;

@Configuration
public class FcEventConfig {

    @ConditionalOnMissingBean(IFcSourceEventListener.class)
    @Bean
    public IFcSourceEventListener fcSourceEventListener(List<IFcCacheSource> sources) {
        return new FcSourceEventListener(sources);
    }





}
