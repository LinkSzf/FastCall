package priv.szf.fastcall.core.declarative.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method argument to a {@code {name}} placeholder of the request URI template.
 * The value is URL-encoded; under {@code @FcMethod.uri} placeholders and bindings must match one to one.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcPath {

    /**
     * Placeholder name to replace, for example {@code id} for {@code /users/{id}}.
     * When blank, the Java parameter name is used and the code must be compiled with {@code -parameters}.
     */
    String value() default "";
}
