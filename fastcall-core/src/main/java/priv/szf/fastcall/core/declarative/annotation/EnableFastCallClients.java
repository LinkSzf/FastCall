package priv.szf.fastcall.core.declarative.annotation;

import priv.szf.fastcall.core.declarative.FcClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(FcClientRegistrar.class)
public @interface EnableFastCallClients {

    /**
     * 扫描包路径。
     */
    String[] basePackages() default {};

    /**
     * 扫描锚点类。
     */
    Class<?>[] basePackageClasses() default {};
}
