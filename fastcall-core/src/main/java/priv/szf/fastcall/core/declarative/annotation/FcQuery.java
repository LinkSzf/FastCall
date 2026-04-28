package priv.szf.fastcall.core.declarative.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcQuery {

    /**
     * Query参数名。
     * 对单值参数：优先使用该值作为参数名，留空时回退到Java参数名。
     * 对Map或Bean参数：无论是否填写该值，都会按键值对展开。
     */
    String value() default "";
}
