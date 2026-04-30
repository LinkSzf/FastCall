package priv.szf.fastcall.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.core.event.FcSourceEventListener;
import priv.szf.fastcall.core.event.IFcSourceEventListener;

import java.util.List;

@Slf4j
@Configuration
public class FcEventConfig {

    private static final String SOURCE_EVENT_LISTENER = FastCallConsts.NAME + "SourceEventListener";

    @ConditionalOnMissingBean(IFcSourceEventListener.class)
    @Bean(SOURCE_EVENT_LISTENER)
    public IFcSourceEventListener fcSourceEventListener(
            List<IFcCacheSource> sources,
            FastCallClientFactory clientFactory,
            ObjectProvider<FcDigestAuthProvider> digestAuthProvider
    ) {
        log.info("{} default source event listener initialized.", FastCallConsts.NAME);
        return new FcSourceEventListener(sources, clientFactory, digestAuthProvider.getIfAvailable());
    }





}

