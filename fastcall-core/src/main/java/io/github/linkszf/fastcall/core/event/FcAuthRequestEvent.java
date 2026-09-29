package io.github.linkszf.fastcall.core.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import io.github.linkszf.fastcall.common.event.request.IFcAuthRequestEvent;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class FcAuthRequestEvent extends FcRequestEvent implements IFcAuthRequestEvent {

    private final String system;

}
