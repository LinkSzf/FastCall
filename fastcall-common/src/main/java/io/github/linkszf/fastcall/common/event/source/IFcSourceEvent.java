package io.github.linkszf.fastcall.common.event.source;

import io.github.linkszf.fastcall.common.event.IFcEvent;

public interface IFcSourceEvent extends IFcEvent {

    String getSystem();

    FcSourceEventType getEventType();

}
