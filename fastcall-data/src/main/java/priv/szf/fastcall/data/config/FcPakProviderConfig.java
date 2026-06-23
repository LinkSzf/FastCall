package priv.szf.fastcall.data.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;
import priv.szf.fastcall.data.provider.FcDefaultPakProvider;
import priv.szf.fastcall.data.provider.FcPakMapping;

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
            FcHeaderAssignDao headerAssignDao,
            FcPakMapping pakMapping
    ) {
        log.info("{} default pak provider initialized.", FastCallConsts.NAME);
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

