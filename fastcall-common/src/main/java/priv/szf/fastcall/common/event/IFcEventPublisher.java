package priv.szf.fastcall.common.event;

import java.util.Collection;

public interface IFcEventPublisher {

    void publish(IFcEvent event);

    void publishAll(Collection<IFcEvent> events);

}
