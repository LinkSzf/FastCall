package io.github.linkszf.fastcall.data.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.data.event.FcApiRequestEventListener;
import io.github.linkszf.fastcall.data.event.FcAuthRequestEventListener;
import io.github.linkszf.fastcall.data.event.IFcApiRequestEventListener;
import io.github.linkszf.fastcall.data.event.IFcAuthRequestEventListener;
import io.github.linkszf.fastcall.data.mapper.FcApiDao;
import io.github.linkszf.fastcall.data.mapper.FcAuthDao;
import io.github.linkszf.fastcall.data.mapper.FcSystemDao;

@Configuration
public class FcDataEventConfig {

    private static final String AuthRequestEventListener = FastCallConsts.NAME + "AuthRequestEventListener";

    private static final String ApiRequestEventListener = FastCallConsts.NAME + "ApiRequestEventListener";

    @ConditionalOnProperty(prefix = "fast-call.filter", name = "enable-request-event", havingValue = "true")
    @Bean(FastCallConsts.EVENT_ENABLE)
    public Object fcEventEnable() {
        return new Object();
    }

    @ConditionalOnBean(name = FastCallConsts.EVENT_ENABLE)
    @ConditionalOnMissingBean(IFcAuthRequestEventListener.class)
    @Bean(AuthRequestEventListener)
    public IFcAuthRequestEventListener fcAuthRequestEventListener (
            FcSystemDao systemDao,
            FcAuthDao authDao
    ) {
        return new FcAuthRequestEventListener(systemDao, authDao);
    }

    @ConditionalOnBean(name = FastCallConsts.EVENT_ENABLE)
    @ConditionalOnMissingBean(IFcApiRequestEventListener.class)
    @Bean(ApiRequestEventListener)
    public IFcApiRequestEventListener fcApiRequestEventListener(
            FcSystemDao systemDao,
            FcApiDao apiDao
    ) {
        return new FcApiRequestEventListener(systemDao, apiDao);
    }

}
