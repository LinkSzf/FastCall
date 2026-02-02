package priv.szf.fastcall.core.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import priv.szf.fastcall.common.FastCallConts;
import priv.szf.fastcall.common.event.source.FcSourceEventType;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.config.FcAsyncConfig;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FcSourceEventListener implements IFcSourceEventListener {

    private final List<IFcCacheSource> sources;

    private final FastCallClientFactory clientFactory;

    @Async(FcAsyncConfig.EVENT_EXECUTOR)
    @EventListener
    @Override
    public void listen(IFcSourceEvent event) {
        log.debug("{}-a source event is listened: system[{}]", FastCallConts.NAME, event.getSystem());
        Optional.of(event)
                .map(IFcSourceEvent::getSystem)
                .ifPresent(system -> {
                    sources.forEach(s -> s.invalidate(system));
                    if (event.getEventType() == FcSourceEventType.DELETE) {
                        clientFactory.removeClient(system);
                    }
                });

    }

}
