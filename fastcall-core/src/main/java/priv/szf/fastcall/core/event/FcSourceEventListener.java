package priv.szf.fastcall.core.event;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import priv.szf.fastcall.common.event.source.FcSourceEvent;
import priv.szf.fastcall.core.source.IFcCacheSource;
import priv.szf.fastcall.core.config.FcAsyncConfig;
import priv.szf.fastcall.common.event.IFcEventLister;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcSourceEventListener implements IFcEventLister<FcSourceEvent> {

    private final List<IFcCacheSource> sources;

    @Async(FcAsyncConfig.EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Override
    public void listen(FcSourceEvent event) {
        Optional.of(event)
                .map(FcSourceEvent::getSystemCode)
                .ifPresent(systemCode ->
                        sources.forEach(s -> s.invalidate(systemCode))
                );
    }

}
