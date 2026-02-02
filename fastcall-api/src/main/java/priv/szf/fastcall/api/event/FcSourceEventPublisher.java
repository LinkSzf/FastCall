package priv.szf.fastcall.api.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.event.FcBaseEventPublisher;
import priv.szf.fastcall.common.event.IFcEventPublisher;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;

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
