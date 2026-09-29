package io.github.linkszf.fastcall.core.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.event.FcBaseEventPublisher;
import io.github.linkszf.fastcall.common.event.request.IFcRequestEvent;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Slf4j
public class FcRequestEventPublisher extends FcBaseEventPublisher<IFcRequestEvent> implements IFcRequestEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Executor executor;

    @Override
    protected void doPublish(IFcRequestEvent event) {
        executor.execute(() -> {
            applicationEventPublisher.publishEvent(event);
            log.debug("{}-a request event published: url[{}]", FastCallConsts.NAME, event.getUrl());
        });

    }

}
