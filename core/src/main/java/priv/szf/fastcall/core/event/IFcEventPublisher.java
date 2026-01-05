package priv.szf.fastcall.core.event;

import java.util.Collection;

public interface IFcEventPublisher {

    void publish(IFcEvent event);

    void publishAll(Collection<IFcEvent> events);

}
