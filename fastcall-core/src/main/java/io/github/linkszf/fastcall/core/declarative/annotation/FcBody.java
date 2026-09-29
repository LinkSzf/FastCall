package io.github.linkszf.fastcall.core.declarative.annotation;

import io.github.linkszf.fastcall.common.FcMediaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method argument as the whole request body, serialized with the declared media type.
 * At most one per method, and never together with {@code @FcPart}.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcBody {

    /**
     * Media type of the body, defaulting to {@code application/json}.
     * With {@code APPLICATION_FORM_URLENCODED} the argument must be a {@code Map} and is sent as a form body.
     */
    FcMediaType mediaType() default FcMediaType.APPLICATION_JSON;
}
