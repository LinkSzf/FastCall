package io.github.linkszf.fastcall.data.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.event.request.IFcAuthRequestEvent;
import io.github.linkszf.fastcall.data.entity.FcAuth;
import io.github.linkszf.fastcall.data.entity.FcSystem;
import io.github.linkszf.fastcall.data.mapper.FcAuthDao;
import io.github.linkszf.fastcall.data.mapper.FcSystemDao;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class FcAuthRequestEventListener implements IFcAuthRequestEventListener {

    private final FcSystemDao systemDao;

    private final FcAuthDao authDao;

    @Async(FastCallConsts.ASYNC_EXECUTOR)
    @EventListener
    @Override
    public void listen(IFcAuthRequestEvent event) {
        log.debug("An auth request event has been listened: system{}", event.getSystem());
        if (!event.isSuccess()) {
            return;
        }
        String systemCode = event.getSystem();
        FcSystem system = systemDao.getByCode(systemCode);
        if (Objects.isNull(system)) {
            return;
        }

        FcAuth auth = authDao.getBySystemId(system.getId());
        if (Objects.isNull(auth)) {
            return;
        }
        auth.setLastAccessTime(event.getOccurredOn());
        authDao.updateOneById(auth.getId(), auth);
    }
}
