package priv.szf.fastcall.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.auth.IFcAuthHandler;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.core.auth.interceptor.FcAuthInterceptor;
import priv.szf.fastcall.core.auth.interceptor.FcAuthSupporter;
import priv.szf.fastcall.core.auth.interceptor.FcAuthRefreshInterceptor;
import priv.szf.fastcall.core.source.FcSourceDelegate;

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
