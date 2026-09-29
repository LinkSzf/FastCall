package priv.szf.fastcall.core.declarative.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an interface as a FastCall declarative client bound to a single FastCall system.
 * Scanned by {@code @EnableFastCallClients} and exposed as a singleton JDK proxy.
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcClient {

    /**
     * Bean name of the generated proxy.
     * Defaults to the decapitalized interface simple name, then to the fully qualified name when that bean name is taken.
     */
    String value() default "";

    /**
     * FastCall system code the client is bound to, which must not be blank.
     */
    String system();
}
