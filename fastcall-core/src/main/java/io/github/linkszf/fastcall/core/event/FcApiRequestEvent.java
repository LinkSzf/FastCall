package io.github.linkszf.fastcall.core.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import io.github.linkszf.fastcall.common.event.request.IFcApiRequestEvent;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class FcApiRequestEvent extends FcRequestEvent implements IFcApiRequestEvent {

    private final String system;

    private final String api;

}
