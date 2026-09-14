package priv.szf.fastcall.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.json.FcJsonCodec;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.support.FcJacksonJsonCodec;

import javax.annotation.PostConstruct;

@Slf4j
@AutoConfigureOrder(FastCallConsts.AUTO_CONFIGURATION_CORE_ORDER)
@EnableConfigurationProperties(FastCallProperties.class)
@ComponentScan(basePackageClasses = FastCallCoreAutoConfiguration.class)
@Configuration
public class FastCallCoreAutoConfiguration {


    @ConditionalOnMissingBean(FcJsonCodec.class)
    @Bean
    public FcJsonCodec fcJsonCodec() {
        return new FcJacksonJsonCodec();
    }

    @PostConstruct
    public void init() {
        log.info("{} is now automatically configured and available.", FastCallConsts.NAME);
    }


}

