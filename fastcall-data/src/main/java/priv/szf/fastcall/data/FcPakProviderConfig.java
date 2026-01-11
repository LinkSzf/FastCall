package priv.szf.fastcall.data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.data.mapper.FastCallDao;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;
import priv.szf.fastcall.data.pak.FcDefaultPakProvider;
import priv.szf.fastcall.data.pak.FcPakMapping;

@Configuration
public class FcPakProviderConfig {

    @ConditionalOnMissingBean
    @Bean
    public IFcPakProvider getPakProvider(
            FcSystemDao systemDao,
            FcAuthDao authDao,
            FcApiDao apiDao,
            FcApiParamDao apiParamDao,
            FcHeaderAssignDao headerAssignDao,
            FcPakMapping pakMapping
    ) {
        return new FcDefaultPakProvider(
                systemDao,
                authDao,
                apiDao,
                apiParamDao,
                headerAssignDao,
                pakMapping
        );
    }





}
