package priv.szf.fastcall.core.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.core.event.FcRequestEventPublisher;
import priv.szf.fastcall.core.event.FcSourceEventListener;
import priv.szf.fastcall.core.event.IFcRequestEventPublisher;
import priv.szf.fastcall.core.event.IFcSourceEventListener;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
public class FcCoreEventConfig {

    private static final String REQUEST_EVENT_PUBLISHER = FastCallConsts.NAME + "RequestEventPublisher";

    private static final String SOURCE_EVENT_LISTENER = FastCallConsts.NAME + "SourceEventListener";

    @ConditionalOnBean(name = FastCallConsts.EVENT_ENABLE)
    @ConditionalOnMissingBean(IFcRequestEventPublisher.class)
    @Bean(REQUEST_EVENT_PUBLISHER)
    public IFcRequestEventPublisher fcRequestEventPublisher (
            ApplicationEventPublisher applicationEventPublisher,
            @Qualifier(FastCallConsts.ASYNC_EXECUTOR) Executor executor
    ) {
        return new FcRequestEventPublisher(applicationEventPublisher, executor);
    }

    @ConditionalOnMissingBean(IFcSourceEventListener.class)
    @Bean(SOURCE_EVENT_LISTENER)
    public IFcSourceEventListener fcSourceEventListener(
            List<IFcCacheSource> sources,
            FastCallClientFactory clientFactory,
            ObjectProvider<FcDigestAuthProvider> digestAuthProvider
    ) {
        return new FcSourceEventListener(sources, clientFactory, digestAuthProvider.getIfAvailable());
    }

}

