package priv.szf.fastcall.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
@ComponentScan(basePackageClasses = FcApiAutoConfiguration.class)
public class FcApiAutoConfiguration {

}
