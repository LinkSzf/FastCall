package priv.szf.fastcall.core.event;

import java.time.LocalDateTime;

public interface IFcEvent {
    String getEventId();

    LocalDateTime getOccurredOn();

    String getEventType();

}
