package io.github.linkszf.fastcall.api.event;

import io.github.linkszf.fastcall.common.event.source.FcSourceEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FcSourceEventCut {

    int idIndex();

    IdLevel level() default IdLevel.SYSTEM;

    FcSourceEventType type() default FcSourceEventType.UPDATE;
}
