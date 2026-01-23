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
            throw new FcUnexpectedException("发布事件时event为空");
        }
        doPublish((T) event);
    }

    @Override
    public void publishAll(Collection<IFcEvent> events) {
        if (CollectionUtil.isEmpty(events)) {
            throw new FcUnexpectedException("发布事件时event为空");
        }

        events.forEach(this::publish);
    }
}
