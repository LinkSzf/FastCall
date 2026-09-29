package priv.szf.fastcall.common.event;

import cn.hutool.core.collection.CollectionUtil;
import priv.szf.fastcall.common.exception.FcUnexpectedException;

import java.util.Collection;
import java.util.Objects;

public abstract class FcBaseEventPublisher<T extends IFcEvent> implements IFcEventPublisher {

    protected abstract void doPublish(T event);

    @Override
    public void publish(IFcEvent event) {
        if (Objects.isNull(event)) {
            throw new FcUnexpectedException("Event must not be null when publishing");
        }
        doPublish((T) event);
    }

    @Override
    public void publishAll(Collection<IFcEvent> events) {
        if (CollectionUtil.isEmpty(events)) {
            throw new FcUnexpectedException("Event must not be null when publishing");
        }

        events.forEach(this::publish);
    }
}
