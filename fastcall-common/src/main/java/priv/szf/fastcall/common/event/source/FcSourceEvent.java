package priv.szf.fastcall.common.event.source;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.common.event.FcBaseEvent;
import priv.szf.fastcall.common.event.IFcEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class FcSourceEvent extends FcBaseEvent implements IFcEvent {

    private final String systemCode;


    @Override
    public String getEventType() {
        return "SOURCE";
    }


}
