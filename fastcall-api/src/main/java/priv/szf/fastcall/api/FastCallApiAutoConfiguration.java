package priv.szf.fastcall.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAspectJAutoProxy
@ConditionalOnWebApplication
@EnableTransactionManagement
@Configuration
@ComponentScan(basePackageClasses = FastCallApiAutoConfiguration.class)
public class FastCallApiAutoConfiguration {

}
