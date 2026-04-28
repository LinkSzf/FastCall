package priv.szf.fastcall.core.declarative.annotation;

import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcMethod {

    /**
     * 可选系统编码覆盖。为空时使用 {@link FcClient#system()}。
     */
    String system() default "";

    /**
     * FastCall配置中的API名称。
     * `api` 与 `uri` 必须且只能设置一个。
     */
    String api() default "";

    /**
     * 请求URI。
     * `api` 与 `uri` 必须且只能设置一个。
     */
    String uri() default "";

    /**
     * 请求方法。若设置了 `api` 且API已配置method，则以API配置为准。
     */
    FcRequestMethod method() default FcRequestMethod.GET;

    /**
     * 可选host覆盖。
     */
    String host() default "";

    /**
     * 是否跳过认证，以匿名模式发起调用。
     */
    boolean anonymous() default false;

    /**
     * 请求体存在时默认的媒体类型。
     */
    FcMediaType bodyType() default FcMediaType.APPLICATION_JSON;
}
