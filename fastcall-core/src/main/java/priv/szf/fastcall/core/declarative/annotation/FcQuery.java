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
     * 为空时参数类型必须是Map，并会合并Map中的所有键值对。
     */
    String value() default "";
}
