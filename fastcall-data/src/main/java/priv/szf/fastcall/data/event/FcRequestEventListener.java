package priv.szf.fastcall.data.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.event.request.IFcApiRequestEvent;
import priv.szf.fastcall.common.event.request.IFcAuthRequestEvent;
import priv.szf.fastcall.common.event.request.IFcRequestEvent;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FcRequestEventListener implements IFcRequestEventListener {

    private final FcAuthDao authDao;

    private final FcApiDao apiDao;

    private final FcSystemDao systemDao;

    @Override
    public void listen(IFcRequestEvent event) {
    }

    @Component
    private class FcAuthRequestEventListener implements IFcAuthRequestEventListener {

        @EventListener
        @Override
        public void listen(IFcAuthRequestEvent event) {
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

    @Component
    private class FcApiRequestEventListener implements IFcApiRequestEventListener {

        @EventListener
        @Override
        public void listen(IFcApiRequestEvent event) {
            if (!event.isSuccess()) {
                return;
            }
            String systemCode = event.getSystem();
            FcSystem system = systemDao.getByCode(systemCode);
            if (Objects.isNull(system)) {
                return;
            }

            String apiName = event.getApi();
            FcApi api = apiDao.getOneByName(apiName);
            if (Objects.isNull(api)) {
                return;
            }

            api.setLastAccessTime(event.getOccurredOn());
            apiDao.updateOneById(api.getId(), api);
        }
    }



}
