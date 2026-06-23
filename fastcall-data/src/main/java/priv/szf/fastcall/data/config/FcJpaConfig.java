package priv.szf.fastcall.data.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import priv.szf.fastcall.data.entity.EntityMarker;
import priv.szf.fastcall.data.mapper.jpa.FcBaseRepository;

import javax.sql.DataSource;

@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(name = "org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean")
@EntityScan(basePackageClasses = EntityMarker.class)
@EnableJpaRepositories(basePackageClasses = FcBaseRepository.class)
@Configuration
public class FcJpaConfig {
}
