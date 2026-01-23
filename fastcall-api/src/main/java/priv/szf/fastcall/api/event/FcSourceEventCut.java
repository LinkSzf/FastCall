package priv.szf.fastcall.api.event;

import priv.szf.fastcall.common.FcSourceEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcSourceEventCut {

    Class<?> entity();

    IdLevel level() default IdLevel.SYSTEM;

    FcSourceEventType type() default FcSourceEventType.UPDATE;
}
