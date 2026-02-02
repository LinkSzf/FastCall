package priv.szf.fastcall.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConts;
import priv.szf.fastcall.core.config.FastCallProperties;

import javax.annotation.PostConstruct;

@Slf4j
@AutoConfigureOrder(FastCallConts.AUTO_CONFIGURATION_CORE_ORDER)
@EnableConfigurationProperties(FastCallProperties.class)
@ComponentScan(basePackageClasses = FastCallCoreAutoConfiguration.class)
@Configuration
public class FastCallCoreAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("{} is now automatically configured and available.", FastCallConts.NAME);
    }


}
