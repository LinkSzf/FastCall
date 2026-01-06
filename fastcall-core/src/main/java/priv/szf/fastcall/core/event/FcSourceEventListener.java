package priv.szf.fastcall.core.event;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import priv.szf.fastcall.common.event.source.FcSourceEvent;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;
import priv.szf.fastcall.core.source.IFcCacheSource;
import priv.szf.fastcall.core.config.FcAsyncConfig;
import priv.szf.fastcall.common.event.IFcEventLister;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcApiParam;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcSourceEventListener implements IFcEventLister<FcSourceEvent<?>> {

    private final FcSystemDao systemDao;

    private final FcAuthDao authDao;

    private final FcApiDao apiDao;

    private final FcApiParamDao apiParamDao;

    private final List<IFcCacheSource> sources;

    private final Map<Class<?>, ISourceProvider> providerMap = init();

    private Map<Class<?>, ISourceProvider> init() {
        Map<Class<?>, ISourceProvider> providerMap = new HashMap<>();
        providerMap.put(FcSystem.class, this::getSystemCodeBySystemId);
        providerMap.put(FcAuth.class, this::getSystemCodeByAuthId);
        providerMap.put(FcApi.class, this::getSystemCodeByApiId);
        providerMap.put(FcApiParam.class, this::getSystemCodeByApiParamId);
        return providerMap;
    }

    @Async(FcAsyncConfig.EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Override
    public void listen(FcSourceEvent<?> event) {
        String name = Thread.currentThread().getName();
        System.out.println(name);
        Class<?> entityType = event.getEntityType();
        ISourceProvider provider = providerMap.get(entityType);
        Optional.ofNullable(provider)
                .map(p -> p.getSystemCode(event.getEntityId()))
                .ifPresent(systemCode -> {
                    sources.forEach(s -> s.invalidate(systemCode));
                });
    }

    @FunctionalInterface
    public interface ISourceProvider {
        String getSystemCode(Long id);
    }

    private String getSystemCodeBySystemId(Long id) {
        FcSystem system = systemDao.getOneById(id);
        return Optional.ofNullable(system)
                .map(FcSystem::getCode)
                .orElse(null);
    }

    private String getSystemCodeByAuthId(Long id) {
        FcAuth auth = authDao.getOneById(id);
        return Optional.ofNullable(auth)
                .map(FcAuth::getSysId)
                .map(this::getSystemCodeBySystemId)
                .orElse(null);
    }

    private String getSystemCodeByApiId(Long id) {
        FcApi api = apiDao.getOneById(id);
        return Optional.ofNullable(api)
                .map(FcApi::getSysId)
                .map(this::getSystemCodeBySystemId)
                .orElse(null);
    }

    private String getSystemCodeByApiParamId(Long id) {
        FcApiParam apiParam = apiParamDao.getOneById(id);
        return Optional.ofNullable(apiParam)
                .map(FcApiParam::getApiId)
                .map(this::getSystemCodeByApiId)
                .orElse(null);
    }




}
