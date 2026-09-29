package io.github.linkszf.fastcall.core.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.event.source.FcSourceEventType;
import io.github.linkszf.fastcall.common.event.source.IFcSourceEvent;
import io.github.linkszf.fastcall.common.source.IFcCacheSource;
import io.github.linkszf.fastcall.core.FastCallClientFactory;
import io.github.linkszf.fastcall.core.auth.interceptor.FcAuthSupporter;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FcSourceEventListener implements IFcSourceEventListener {

    private final List<IFcCacheSource> sources;

    private final FastCallClientFactory clientFactory;

    private final FcAuthSupporter authSupporter;

    @Async(FastCallConsts.ASYNC_EXECUTOR)
    @EventListener
    @Override
    public void listen(IFcSourceEvent event) {
        log.debug("{}-a source event is listened: system[{}]", FastCallConsts.NAME, event.getSystem());
        Optional.of(event)
                .map(IFcSourceEvent::getSystem)
                .ifPresent(system -> {
                    sources.forEach(s -> s.invalidate(system));
                    if (isClientUnavailable(event)) {
                        clientFactory.removeClient(system);
                    }
                    if (event.getEventType() == FcSourceEventType.DELETE) {
                        authSupporter.clearSystemLock(system);
                    }
                });

    }

    private static boolean isClientUnavailable(IFcSourceEvent event) {
        return event.getEventType() == FcSourceEventType.DELETE
                || event.getEventType() == FcSourceEventType.UPDATE;
    }

}
