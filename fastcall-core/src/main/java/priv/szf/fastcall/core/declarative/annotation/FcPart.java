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
public @interface FcPart {

    /**
     * multipart part name.
     */
    String value();

    /**
     * optional part file name. If empty and value is File, the source file name is used.
     */
    String fileName() default "";

    /**
     * part media type for binary/object values.
     */
    FcMediaType mediaType() default FcMediaType.APPLICATION_OCTET_STREAM;
}
