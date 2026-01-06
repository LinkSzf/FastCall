package priv.szf.fastcall.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class FcBaseEvent implements IFcEvent {

    private final String eventId;

    private final LocalDateTime occurredOn;

    protected FcBaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return this.occurredOn;
    }

}
