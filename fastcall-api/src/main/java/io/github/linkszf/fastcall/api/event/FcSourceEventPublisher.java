package io.github.linkszf.fastcall.api.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import io.github.linkszf.fastcall.common.event.FcBaseEventPublisher;
import io.github.linkszf.fastcall.common.event.IFcEventPublisher;
import io.github.linkszf.fastcall.common.event.source.IFcSourceEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcSourceEventPublisher extends FcBaseEventPublisher<IFcSourceEvent> implements IFcEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void doPublish(IFcSourceEvent event) {
        applicationEventPublisher.publishEvent(event);
        log.debug("FcSourceEventPublisher publish event: system[{}], type[{}]", event.getSystem(), event.getEventType());
    }

}
