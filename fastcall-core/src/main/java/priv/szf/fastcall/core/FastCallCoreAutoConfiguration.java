package priv.szf.fastcall.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import priv.szf.fastcall.core.config.FastCallProperties;

@EnableTransactionManagement
@EnableConfigurationProperties(FastCallProperties.class)
@Configuration
@ComponentScan(basePackageClasses = FastCallCoreAutoConfiguration.class)
public class FastCallCoreAutoConfiguration {

}
