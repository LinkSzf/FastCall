package priv.szf.fastcall.core.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import priv.szf.fastcall.common.event.request.IFcAuthRequestEvent;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class FcAuthRequestEvent extends FcRequestEvent implements IFcAuthRequestEvent {

    private final String system;

}
