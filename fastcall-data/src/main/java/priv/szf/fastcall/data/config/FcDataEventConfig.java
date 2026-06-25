package priv.szf.fastcall.data.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.data.event.FcApiRequestEventListener;
import priv.szf.fastcall.data.event.FcAuthRequestEventListener;
import priv.szf.fastcall.data.event.IFcApiRequestEventListener;
import priv.szf.fastcall.data.event.IFcAuthRequestEventListener;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

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
