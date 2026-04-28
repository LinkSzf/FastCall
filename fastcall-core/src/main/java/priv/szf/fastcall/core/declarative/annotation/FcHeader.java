package priv.szf.fastcall.core.declarative.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcHeader {

    /**
     * Header名称。
     * 对单值参数：必须显式填写该值作为参数名。
     * 对Map或Bean参数：可留空，按键值对展开。
     */
    String value() default "";
}
