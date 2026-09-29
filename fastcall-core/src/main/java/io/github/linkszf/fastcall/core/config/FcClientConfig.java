package io.github.linkszf.fastcall.core.config;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.core.FastCall;
import io.github.linkszf.fastcall.core.FastCallClientFactory;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthInterceptor;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthRefreshInterceptor;
import io.github.linkszf.fastcall.core.filter.FcFilterManager;
import io.github.linkszf.fastcall.core.source.FcSourceDelegate;
import io.github.linkszf.fastcall.core.support.FcHttpRequestSupport;
import io.github.linkszf.fastcall.core.support.FcHttpResponseSupport;
import io.github.linkszf.fastcall.common.json.FcJsonCodec;

@Configuration
public class FcClientConfig {

    @Bean
    public FcHttpRequestSupport fcHttpRequestSupport(FcJsonCodec jsonCodec) {
        return new FcHttpRequestSupport(jsonCodec);
    }

    @Bean
    public FcHttpResponseSupport fcHttpResponseSupport(FcJsonCodec jsonCodec) {
        return new FcHttpResponseSupport(jsonCodec);
    }

    @Bean
    public FastCallClientFactory fastCallClientFactory(
        FcSourceDelegate source,
        FastCallProperties properties,
        ConnectionPool connectionPool,
        Dispatcher dispatcher,
        FcAuthInterceptor authInterceptor,
        FcAuthRefreshInterceptor tokenRefreshInterceptor,
        FcFilterManager filterManager,
        FcHttpRequestSupport requestSupport,
        FcHttpResponseSupport responseSupport
    ) {
        return new FastCallClientFactory(
                source,
                properties,
                connectionPool,
                dispatcher,
                authInterceptor,
                tokenRefreshInterceptor,
                filterManager,
                requestSupport,
                responseSupport
        );
    }

    @Bean
    public FastCall fastCall(FastCallClientFactory clientFactory) {
        return new FastCall(clientFactory);
    }




}
