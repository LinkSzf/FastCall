package priv.szf.fastcall.data.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import priv.szf.fastcall.common.event.request.IFcApiRequestEvent;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class FcApiRequestEventListener implements IFcApiRequestEventListener {

    private final FcSystemDao systemDao;

    private final FcApiDao apiDao;

    @EventListener
    @Override
    public void listen(IFcApiRequestEvent event) {
        log.debug("An api request event has been listened: system{}", event.getSystem());
        if (!event.isSuccess()) {
            return;
        }
        String systemCode = event.getSystem();
        FcSystem system = systemDao.getByCode(systemCode);
        if (Objects.isNull(system)) {
            return;
        }

        String apiName = event.getApi();
        FcApi api = apiDao.getOneByNameAndSysId(system.getId(), apiName);
        if (Objects.isNull(api)) {
            return;
        }

        api.setLastAccessTime(event.getOccurredOn());
        apiDao.updateOneById(api.getId(), api);
    }
}
