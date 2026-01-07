package priv.szf.fastcall.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.core.config.FastCallProperties;

import javax.annotation.PostConstruct;

@EnableConfigurationProperties(FastCallProperties.class)
@ComponentScan(basePackageClasses = FastCallCoreAutoConfiguration.class)
@Configuration
public class FastCallCoreAutoConfiguration {
@PostConstruct
    public void init() {
        System.out.println();
    }
}
