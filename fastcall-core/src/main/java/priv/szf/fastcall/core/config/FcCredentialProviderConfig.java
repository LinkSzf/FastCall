package priv.szf.fastcall.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.core.auth.provider.FcApiKeyCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcBasicCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcCookieCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcJwtCredentialProvider;
import priv.szf.fastcall.core.auth.provider.FcTokenCredentialProvider;

@Configuration
public class FcCredentialProviderConfig {


    @ConditionalOnMissingBean(FcApiKeyCredentialProvider.class)
    @Bean
    public FcApiKeyCredentialProvider fcApiKeyCredentialProvider() {
        return new FcApiKeyCredentialProvider();
    }

    @ConditionalOnMissingBean(FcBasicCredentialProvider.class)
    @Bean
    public FcBasicCredentialProvider fcBasicCredentialProvider() {
        return new FcBasicCredentialProvider();
    }

    @ConditionalOnMissingBean(FcCookieCredentialProvider.class)
    @Bean
    public FcCookieCredentialProvider fcCookieCredentialProvider() {
        return new FcCookieCredentialProvider();
    }

    @ConditionalOnMissingBean(FcJwtCredentialProvider.class)
    @Bean
    public FcJwtCredentialProvider fcJwtCredentialProvider() {
        return new FcJwtCredentialProvider();
    }

    @ConditionalOnMissingBean(FcTokenCredentialProvider.class)
    @Bean
    public FcTokenCredentialProvider fcTokenCredentialProvider() {
        return new FcTokenCredentialProvider();
    }

}
