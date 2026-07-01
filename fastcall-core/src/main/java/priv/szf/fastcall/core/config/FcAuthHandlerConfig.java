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
import priv.szf.fastcall.core.auth.provider.FcApiKeyCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcBasicCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcCookieCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcJwtCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcTokenCredentialProvider;

@Configuration
public class FcAuthHandlerConfig {

    @ConditionalOnMissingBean(FcNoneAuthHandler.class)
    @Bean
    public FcNoneAuthHandler fcNoneAuthHandler() {
        return new FcNoneAuthHandler();
    }

    @ConditionalOnMissingBean(FcApiKeyAuthHandler.class)
    @Bean
    public FcApiKeyAuthHandler fcApiKeyAuthHandler(FcApiKeyCredentialProvider credentialProvider) {
        return new FcApiKeyAuthHandler(credentialProvider);
    }

    @ConditionalOnMissingBean(FcBasicAuthHandler.class)
    @Bean
    public FcBasicAuthHandler fcBasicAuthHandler(FcBasicCredentialProvider credentialProvider) {
        return new FcBasicAuthHandler(credentialProvider);
    }

    @ConditionalOnMissingBean(FcTokenAuthHandler.class)
    @Bean
    public FcTokenAuthHandler fcTokenAuthHandler(FcTokenCredentialProvider credentialProvider) {
        return new FcTokenAuthHandler(credentialProvider);
    }

    @ConditionalOnMissingBean(FcCookieAuthHandler.class)
    @Bean
    public FcCookieAuthHandler fcCookieAuthHandler(FcCookieCredentialProvider credentialProvider) {
        return new FcCookieAuthHandler(credentialProvider);
    }

    @ConditionalOnMissingBean(FcJwtAuthHandler.class)
    @Bean
    public FcJwtAuthHandler fcJwtAuthHandler(FcJwtCredentialProvider credentialProvider) {
        return new FcJwtAuthHandler(credentialProvider);
    }

}
