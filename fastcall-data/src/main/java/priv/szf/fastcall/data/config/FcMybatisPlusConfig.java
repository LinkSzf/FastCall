package priv.szf.fastcall.data.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import priv.szf.fastcall.data.mapper.mybatisplus.FcBaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(name = "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration")
@MapperScan(basePackageClasses = FcBaseMapper.class)
@Configuration
public class FcMybatisPlusConfig {

}
