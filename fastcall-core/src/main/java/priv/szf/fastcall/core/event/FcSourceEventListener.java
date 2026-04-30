package priv.szf.fastcall.core.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.event.source.FcSourceEventType;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;
import priv.szf.fastcall.common.source.IFcCacheSource;
import priv.szf.fastcall.core.FastCallClientFactory;
import priv.szf.fastcall.core.auth.handler.FcBaseRefreshableAuthHandler;
import priv.szf.fastcall.core.auth.provider.FcDigestAuthProvider;
import priv.szf.fastcall.core.config.FcAsyncConfig;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class FcSourceEventListener implements IFcSourceEventListener {

    private final List<IFcCacheSource> sources;

    private final FastCallClientFactory clientFactory;

    private final FcDigestAuthProvider digestAuthProvider;

    @Async(FcAsyncConfig.EVENT_EXECUTOR)
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
                        FcBaseRefreshableAuthHandler.clearSystemLock(system);
                        Optional.ofNullable(digestAuthProvider)
                                .ifPresent(provider -> provider.removeNonceManager(system));
                    }
                });

    }

    private static boolean isClientUnavailable(IFcSourceEvent event) {
        return event.getEventType() == FcSourceEventType.DELETE
                || event.getEventType() == FcSourceEventType.UPDATE;
    }

}
