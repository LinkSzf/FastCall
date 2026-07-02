package priv.szf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.core.auth.handler.FcApiKeyAuthHandler;
import priv.szf.fastcall.core.auth.handler.FcBasicAuthHandler;
import priv.szf.fastcall.core.auth.handler.FcCookieAuthHandler;
import priv.szf.fastcall.core.auth.handler.FcJwtAuthHandler;
import priv.szf.fastcall.core.auth.handler.FcNoneAuthHandler;
import priv.szf.fastcall.core.auth.handler.FcTokenAuthHandler;

@Configuration
public class FcAuthHandlerConfig {

    @ConditionalOnMissingBean(FcNoneAuthHandler.class)
    @Bean
    public FcNoneAuthHandler fcNoneAuthHandler() {
        return new FcNoneAuthHandler();
    }

    @ConditionalOnMissingBean(FcApiKeyAuthHandler.class)
    @Bean
    public FcApiKeyAuthHandler fcApiKeyAuthHandler() {
        return new FcApiKeyAuthHandler();
    }

    @ConditionalOnMissingBean(FcBasicAuthHandler.class)
    @Bean
    public FcBasicAuthHandler fcBasicAuthHandler() {
        return new FcBasicAuthHandler();
    }

    @ConditionalOnMissingBean(FcTokenAuthHandler.class)
    @Bean
    public FcTokenAuthHandler fcTokenAuthHandler() {
        return new FcTokenAuthHandler();
    }

    @ConditionalOnMissingBean(FcCookieAuthHandler.class)
    @Bean
    public FcCookieAuthHandler fcCookieAuthHandler() {
        return new FcCookieAuthHandler();
    }

    @ConditionalOnMissingBean(FcJwtAuthHandler.class)
    @Bean
    public FcJwtAuthHandler fcJwtAuthHandler() {
        return new FcJwtAuthHandler();
    }

}
