package priv.szf.fastcall.data;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import priv.szf.fastcall.data.mapper.mybatisplus.FcBaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


@ConditionalOnClass(name = "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration")
@ConditionalOnBean(DataSource.class)
@MapperScan(basePackageClasses = FcBaseMapper.class)
@Configuration
public class FcMybatisPlusConfig {

}
