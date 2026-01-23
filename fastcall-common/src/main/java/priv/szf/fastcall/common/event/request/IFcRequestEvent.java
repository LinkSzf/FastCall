package priv.szf.fastcall.common.event.request;

import priv.szf.fastcall.common.event.IFcEvent;

public interface IFcRequestEvent extends IFcEvent {

    String getUrl();

    boolean isSuccess();


}
