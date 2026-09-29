package io.github.linkszf.fastcall.data.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.source.IFcPakProvider;
import io.github.linkszf.fastcall.data.mapper.FcApiDao;
import io.github.linkszf.fastcall.data.mapper.FcApiParamDao;
import io.github.linkszf.fastcall.data.mapper.FcAuthDao;
import io.github.linkszf.fastcall.data.mapper.FcRateLimitDao;
import io.github.linkszf.fastcall.data.mapper.FcRetryDao;
import io.github.linkszf.fastcall.data.mapper.FcSystemDao;
import io.github.linkszf.fastcall.data.provider.FcDefaultPakProvider;
import io.github.linkszf.fastcall.data.provider.FcPakMapping;

@Slf4j
@Configuration
public class FcPakProviderConfig {

    private static final String PAK_PROVIDER = FastCallConsts.NAME + "PakProvider";

    @ConditionalOnMissingBean
    @Bean(PAK_PROVIDER)
    public IFcPakProvider fcPakProvider(
            FcSystemDao systemDao,
            FcAuthDao authDao,
            FcApiDao apiDao,
            FcApiParamDao apiParamDao,
            FcRateLimitDao rateLimitDao,
            FcRetryDao retryDao,
            FcPakMapping pakMapping
    ) {
        log.info("{} default pak provider initialized.", FastCallConsts.NAME);
        return new FcDefaultPakProvider(
                systemDao,
                authDao,
                apiDao,
                apiParamDao,
                rateLimitDao,
                retryDao,
                pakMapping
        );
    }





}

