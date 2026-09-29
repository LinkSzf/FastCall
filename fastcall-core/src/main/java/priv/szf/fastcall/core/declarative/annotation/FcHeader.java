package priv.szf.fastcall.core.declarative.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a method argument to the request headers, or expands a {@code Map}/bean argument into several headers.
 * Plain arguments need an explicit name; iterable values emit one header per element.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcHeader {

    /**
     * Header name, required for arguments that are not a {@code Map} or bean.
     * A {@code Map} or bean expands entry by entry, but a value serializing to a single scalar still needs the name.
     */
    String value() default "";
}
