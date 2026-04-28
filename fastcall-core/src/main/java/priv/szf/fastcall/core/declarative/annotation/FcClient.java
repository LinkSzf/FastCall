package priv.szf.fastcall.core.declarative.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcClient {

    /**
     * 可选Bean名称。
     */
    String value() default "";

    /**
     * FastCall系统编码。
     */
    String system();
}
