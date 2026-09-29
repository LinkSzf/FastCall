package io.github.linkszf.fastcall.common.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@SuperBuilder
public abstract class FcBaseEvent implements IFcEvent {

    private final String eventId = UUID.randomUUID().toString();

    private final LocalDateTime occurredOn = LocalDateTime.now();

}
