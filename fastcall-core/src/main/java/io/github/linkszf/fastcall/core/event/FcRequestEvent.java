package io.github.linkszf.fastcall.core.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import io.github.linkszf.fastcall.common.event.FcBaseEvent;
import io.github.linkszf.fastcall.common.event.request.IFcRequestEvent;

@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class FcRequestEvent extends FcBaseEvent implements IFcRequestEvent {

    private final String url;

    private final boolean success;

}
