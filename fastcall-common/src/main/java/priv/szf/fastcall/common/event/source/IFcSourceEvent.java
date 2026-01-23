package priv.szf.fastcall.common.event.source;

import priv.szf.fastcall.common.FcSourceEventType;
import priv.szf.fastcall.common.event.IFcEvent;

public interface IFcSourceEvent extends IFcEvent {

    String getSystem();

    FcSourceEventType getEventType();

}
