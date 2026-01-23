package priv.szf.fastcall.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.common.FcSourceEventType;
import priv.szf.fastcall.common.event.FcBaseEvent;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcSourceEvent extends FcBaseEvent implements IFcSourceEvent {

    private final String system;

    private final FcSourceEventType eventType;


    @Override
    public FcSourceEventType getEventType() {
        return eventType;
    }


}
