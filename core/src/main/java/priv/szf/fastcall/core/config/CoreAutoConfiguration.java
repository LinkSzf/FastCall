package priv.szf.fastcall.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableConfigurationProperties(FastCallProperties.class)
@Configuration
@ComponentScan(basePackages = "priv.szf.fastcall.core")
public class CoreAutoConfiguration {

}
