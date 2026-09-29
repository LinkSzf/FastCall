package io.github.linkszf.fastcall.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.linkszf.fastcall.common.event.source.FcSourceEventType;
import io.github.linkszf.fastcall.common.event.FcBaseEvent;
import io.github.linkszf.fastcall.common.event.source.IFcSourceEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcSourceEvent extends FcBaseEvent implements IFcSourceEvent {

    private final String system;

    private final FcSourceEventType eventType;

}
