package priv.szf.fastcall.core.declarative.annotation;

import priv.szf.fastcall.common.FcMediaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcBody {

    /**
     * 请求体媒体类型。
     */
    FcMediaType mediaType() default FcMediaType.APPLICATION_JSON;
}
