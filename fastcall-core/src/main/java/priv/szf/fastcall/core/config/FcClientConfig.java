package priv.szf.fastcall.core.config;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.auth.interceptor.FcAuthInterceptor;
import priv.szf.fastcall.core.auth.interceptor.FcAuthRefreshInterceptor;
import priv.szf.fastcall.core.filter.FcFilterManager;
import priv.szf.fastcall.core.source.FcSourceDelegate;

@Configuration
public class FcClientConfig {

    @Bean
    public FastCallClientFactory fastCallClientFactory(
        FcSourceDelegate source,
        FastCallProperties properties,
        ConnectionPool connectionPool,
        Dispatcher dispatcher,
        FcAuthInterceptor authInterceptor,
        FcAuthRefreshInterceptor tokenRefreshInterceptor,
        FcFilterManager filterManager
    ) {
        return new FastCallClientFactory(
                source,
                properties,
                connectionPool,
                dispatcher,
                authInterceptor,
                tokenRefreshInterceptor,
                filterManager
        );
    }

    @Bean
    public FastCall fastCall(FastCallClientFactory clientFactory) {
        return new FastCall(clientFactory);
    }




}
