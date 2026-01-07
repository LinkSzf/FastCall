package priv.szf.fastcall.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableAspectJAutoProxy
@ConditionalOnWebApplication
@Configuration
@ComponentScan(basePackageClasses = FastCallApiAutoConfiguration.class)
public class FastCallApiAutoConfiguration {

}
