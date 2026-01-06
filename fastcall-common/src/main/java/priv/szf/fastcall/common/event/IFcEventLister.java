package priv.szf.fastcall.common.event;

public interface IFcEventLister<T extends IFcEvent> {

    void listen(T event);


}
