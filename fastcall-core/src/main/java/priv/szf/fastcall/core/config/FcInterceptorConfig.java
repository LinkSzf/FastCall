package priv.szf.fastcall.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.core.auth.handler.FcAuthHandlerDelegate;
import priv.szf.fastcall.core.auth.interceptor.FcAuthInterceptor;
import priv.szf.fastcall.core.auth.interceptor.FcAuthRefreshInterceptor;

@Configuration
public class FcInterceptorConfig {

    @Bean
    public FcAuthInterceptor fcAuthInterceptor(FcAuthHandlerDelegate authHandlerDelegate) {
        return FcAuthInterceptor.builder()
                .authHandler(authHandlerDelegate)
                .build();
    }

    @Bean
    public FcAuthRefreshInterceptor fcAuthRefreshInterceptor(FcAuthHandlerDelegate authHandlerDelegate) {
        return FcAuthRefreshInterceptor.builder()
                .authHandler(authHandlerDelegate)
                .build();
    }





}
