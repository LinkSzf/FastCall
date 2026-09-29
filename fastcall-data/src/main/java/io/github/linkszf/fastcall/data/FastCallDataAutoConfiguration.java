package io.github.linkszf.fastcall.data;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import io.github.linkszf.fastcall.common.FastCallConsts;

@AutoConfigureOrder(FastCallConsts.AUTO_CONFIGURATION_DATA_ORDER)
@Configuration
@ComponentScan(basePackageClasses = FastCallDataAutoConfiguration.class)
public class FastCallDataAutoConfiguration {

}

