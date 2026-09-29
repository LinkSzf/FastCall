package io.github.linkszf.fastcall.core.declarative.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method argument whose runtime value overrides the system and client used for the call.
 * The argument is not sent as request data, and a configured API is still resolved under the declared system.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcAppointedSystem {
}
