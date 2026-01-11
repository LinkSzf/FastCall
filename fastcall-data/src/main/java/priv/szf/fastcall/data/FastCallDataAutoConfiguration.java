package priv.szf.fastcall.data;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConts;

@AutoConfigureOrder(FastCallConts.AUTO_CONFIGURATION_DATA_ORDER)
@Configuration
@ComponentScan(basePackageClasses = FastCallDataAutoConfiguration.class)
public class FastCallDataAutoConfiguration {

}
