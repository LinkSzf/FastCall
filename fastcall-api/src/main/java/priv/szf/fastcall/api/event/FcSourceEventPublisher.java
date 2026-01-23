package priv.szf.fastcall.api.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.event.FcBaseEventPublisher;
import priv.szf.fastcall.common.event.IFcEventPublisher;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;

@RequiredArgsConstructor
@Component
public class FcSourceEventPublisher extends FcBaseEventPublisher<IFcSourceEvent> implements IFcEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void doPublish(IFcSourceEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
