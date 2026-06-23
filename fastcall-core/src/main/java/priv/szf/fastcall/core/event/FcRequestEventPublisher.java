package priv.szf.fastcall.core.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.event.FcBaseEventPublisher;
import priv.szf.fastcall.common.event.request.IFcRequestEvent;

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
