package priv.szf.fastcall.core.event.source;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.event.IFcEvent;
import priv.szf.fastcall.core.event.IFcEventPublisher;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class FcSourceEventPublisher  implements IFcEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(IFcEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishAll(Collection<IFcEvent> events) {
        if (CollectionUtil.isEmpty(events)) {
            return;
        }

        events.forEach(this::publish);
    }
}
