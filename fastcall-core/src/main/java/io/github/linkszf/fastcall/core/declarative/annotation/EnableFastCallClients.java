package io.github.linkszf.fastcall.core.declarative.annotation;

import io.github.linkszf.fastcall.core.declarative.FcClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables scanning for {@code @FcClient} interfaces and registers each one as a singleton proxy bean.
 * Falls back to the package of the annotated class when neither attribute declares a base package.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(FcClientRegistrar.class)
public @interface EnableFastCallClients {

    /**
     * Packages scanned for {@code @FcClient} interfaces. Empty by default, and blank entries are ignored.
     */
    String[] basePackages() default {};

    /**
     * Classes whose package is scanned for {@code @FcClient} interfaces. Empty by default.
     */
    Class<?>[] basePackageClasses() default {};
}
