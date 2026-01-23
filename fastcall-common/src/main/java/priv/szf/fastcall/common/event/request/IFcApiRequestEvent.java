package priv.szf.fastcall.common.event.request;

public interface IFcApiRequestEvent extends IFcAuthRequestEvent {

    String getSystem();

    String getApi();
}
