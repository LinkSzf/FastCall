package priv.szf.fastcall.data.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.event.request.IFcAuthRequestEvent;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcAuthRequestEventListener implements IFcAuthRequestEventListener {

    private final FcAuthDao authDao;

    private final FcSystemDao systemDao;

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
        auth.setLastAccessTime(event.getOccurredOn());
        authDao.updateOneById(auth.getId(), auth);
    }
}
