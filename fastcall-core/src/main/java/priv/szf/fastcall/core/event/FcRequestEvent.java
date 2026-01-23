package priv.szf.fastcall.core.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import priv.szf.fastcall.common.event.FcBaseEvent;
import priv.szf.fastcall.common.event.request.IFcRequestEvent;

@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class FcRequestEvent extends FcBaseEvent implements IFcRequestEvent {

    private final String url;

    private final boolean success;

}
