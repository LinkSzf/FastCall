package priv.szf.fastcall.core.event;

public interface IFcEventLister<T extends IFcEvent> {

    void listen(T event);


}
