package io.github.linkszf.fastcall.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.core.auth.IFcAuthHandler;
import io.github.linkszf.fastcall.core.auth.IFcCredentialProvider;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthInterceptor;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthSupporter;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthRefreshInterceptor;
import io.github.linkszf.fastcall.core.source.FcSourceDelegate;

import java.util.List;

@Configuration
public class FcInterceptorConfig {

    @Bean
    public FcAuthSupporter fcAuthSupporter(
            List<IFcAuthHandler> handlers,
            List<IFcCredentialProvider> providers,
            FcSourceDelegate source
    ) {
        return new FcAuthSupporter(handlers, providers, source);
    }


    @Bean
    public FcAuthInterceptor fcAuthInterceptor(FcAuthSupporter authManager) {
        return FcAuthInterceptor.builder()
                .authSupporter(authManager)
                .build();
    }

    @Bean
    public FcAuthRefreshInterceptor fcAuthRefreshInterceptor(FcAuthSupporter authManager) {
        return FcAuthRefreshInterceptor.builder()
                .authSupporter(authManager)
                .build();
    }





}
