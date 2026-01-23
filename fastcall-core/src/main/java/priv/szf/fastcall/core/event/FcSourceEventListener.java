package priv.szf.fastcall.core.event;


import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.core.config.FcAsyncConfig;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FcSourceEventListener implements IFcSourceEventListener {

    private final List<IFcCacheSource> sources;

    @Async(FcAsyncConfig.EVENT_EXECUTOR)
    @EventListener
    @Override
    public void listen(IFcSourceEvent event) {
        Optional.of(event)
                .map(IFcSourceEvent::getSystem)
                .ifPresent(system ->
                        sources.forEach(s -> s.invalidate(system))
                );
    }

}
