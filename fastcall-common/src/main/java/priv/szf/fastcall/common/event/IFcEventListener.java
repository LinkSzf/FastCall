package priv.szf.fastcall.common.event;

public interface IFcEventListener<T extends IFcEvent> {

    void listen(T event);
}
