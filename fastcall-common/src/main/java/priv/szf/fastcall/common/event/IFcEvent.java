package priv.szf.fastcall.common.event;

import java.time.LocalDateTime;

public interface IFcEvent {
    String getEventId();

    LocalDateTime getOccurredOn();

    String getEventType();

}
