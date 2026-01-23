package priv.szf.fastcall.core.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.event.FcBaseEventPublisher;
import priv.szf.fastcall.common.event.request.IFcRequestEvent;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.config.FcAsyncConfig;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Component
public class FcRequestEventPublisher extends FcBaseEventPublisher<IFcRequestEvent> implements IFcRequestEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final FastCallProperties properties;

    @Autowired
    @Qualifier(FcAsyncConfig.EVENT_EXECUTOR)
    private Executor executor;

    @Override
    protected void doPublish(IFcRequestEvent event) {
        executor.execute(() -> {
            if (properties.isAllowEvent()) {
                applicationEventPublisher.publishEvent(event);
            }
        });

    }

}
