package io.github.linkszf.fastcall.core.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.source.IFcCacheSource;
import io.github.linkszf.fastcall.core.FastCallClientFactory;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthSupporter;
import io.github.linkszf.fastcall.core.event.FcRequestEventPublisher;
import io.github.linkszf.fastcall.core.event.FcSourceEventListener;
import io.github.linkszf.fastcall.core.event.IFcRequestEventPublisher;
import io.github.linkszf.fastcall.core.event.IFcSourceEventListener;

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
            FcAuthSupporter authSupporter
    ) {
        return new FcSourceEventListener(sources, clientFactory, authSupporter);
    }

}

