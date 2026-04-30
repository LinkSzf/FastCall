package priv.szf.fastcall.core.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.event.FcBaseEventPublisher;
import priv.szf.fastcall.common.event.request.IFcRequestEvent;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.config.FcAsyncConfig;

import java.util.concurrent.Executor;

@Slf4j
@Component
public class FcRequestEventPublisher extends FcBaseEventPublisher<IFcRequestEvent> implements IFcRequestEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final FastCallProperties properties;

    private final Executor executor;

    @Autowired
    public FcRequestEventPublisher(
            ApplicationEventPublisher applicationEventPublisher,
            FastCallProperties properties,
            @Qualifier(FcAsyncConfig.EVENT_EXECUTOR) Executor executor
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.properties = properties;
        this.executor = executor;
    }

    @Override
    protected void doPublish(IFcRequestEvent event) {
        executor.execute(() -> {
            if (properties.isAllowEvent()) {
                applicationEventPublisher.publishEvent(event);
                log.debug("{}-a request event published: url[{}]", FastCallConsts.NAME, event.getUrl());
            }
        });

    }

}
