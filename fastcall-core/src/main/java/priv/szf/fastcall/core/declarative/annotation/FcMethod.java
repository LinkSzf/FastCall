package priv.szf.fastcall.core.declarative.annotation;

import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares how one client interface method becomes a FastCall request; required on every abstract method.
 * Exactly one of {@link #api()} and {@link #uri()} must be set.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcMethod {

    /**
     * System code overriding {@link FcClient#system()}; when blank the interface-level system is used.
     */
    String system() default "";

    /**
     * Name of a preconfigured API whose path, method, host and default parameters are applied.
     * Exactly one of it and {@link #uri()} must be set.
     */
    String api() default "";

    /**
     * Request URI resolved against the host, for example {@code /users/{id}}.
     * Exactly one of it and {@link #api()} must be set, and every placeholder needs a matching {@link FcPath}.
     */
    String uri() default "";

    /**
     * Request method of the call, {@code GET} by default.
     * Ignored when {@link #api()} points to an API that already declares a method.
     */
    FcRequestMethod method() default FcRequestMethod.GET;

    /**
     * Host override for this call; when blank the host configured for the system is used.
     * With {@link #api()} set, the API's particular host takes precedence over the system host.
     */
    String host() default "";

    /**
     * Whether the call is sent without authentication, as an anonymous call.
     * Defaults to {@code false}, which applies the authentication configured for the system.
     */
    boolean anonymous() default false;

    /**
     * Media type of the request body, defaulting to {@code application/json}.
     * Overridden by {@link FcBody#mediaType()}, and replaced by multipart when a {@link FcPart} is declared.
     */
    FcMediaType bodyType() default FcMediaType.APPLICATION_JSON;
}
