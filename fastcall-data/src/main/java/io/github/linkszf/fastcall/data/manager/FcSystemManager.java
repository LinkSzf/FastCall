package io.github.linkszf.fastcall.data.manager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.exception.FcDataDuplicatedException;
import io.github.linkszf.fastcall.data.entity.FcAuth;
import io.github.linkszf.fastcall.data.entity.FcRetry;
import io.github.linkszf.fastcall.data.entity.FcSystem;
import io.github.linkszf.fastcall.data.mapper.FcSystemDao;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class FcSystemManager {

    private final FcAuthManager authManager;

    private final FcRetryManager retryManager;

    private final FcApiManager apiManager;

    private final FcSystemDao systemDao;

    public List<FcSystem> listAll() {
        return systemDao.list();
    }

    public List<FcSystem> listAllWithAuth() {
        List<FcSystem> systemList = listAll();
        fillWithAuth(systemList);
        fillWithRetry(systemList);
        return systemList;
    }

    public List<FcSystem> listByIds(Collection<Long> ids) {
        return systemDao.listByIds(ids);
    }

    public List<FcSystem> listByIdsWithAuth(Collection<Long> ids) {
        List<FcSystem> systemList = listByIds(ids);
        fillWithAuth(systemList);
        fillWithRetry(systemList);
        return systemList;
    }

    public FcSystem getById(Long id) {
        return Optional.ofNullable(id)
                .map(systemDao::getOneById)
                .orElse(null);
    }

    public FcSystem getByIdWithAuth(Long id) {
        return Optional.ofNullable(id)
                .map(this::getById)
                .map(system -> {
                    FcAuth auth = authManager.getBySystemId(id);
                    system.setAuth(auth);
                    system.setRetry(retryManager.getBySystemId(id));
                    return system;
                })
                .orElse(null);
    }

    public FcSystem save(@NonNull FcSystem system) {
        checkData(system);

        system.setUpdateAt(LocalDateTime.now());
        FcSystem savedSystem = systemDao.saveOne(system);

        Long systemId = savedSystem.getId();

        if (system.getAuthType() == FcAuthType.NONE) {
            authManager.removeBySystemId(systemId);
        } else {
            FcAuth auth = system.getAuth();
            authManager.save(systemId, auth);
        }

        retryManager.save(systemId, system.getRetry());

        return system;
    }

    public void removeById(Long id) {
        if (Objects.isNull(id)) {
            return;
        }
        authManager.removeBySystemId(id);
        retryManager.removeBySystemId(id);
        apiManager.removeBySystemId(id);
        systemDao.removeById(id);
    }

    private void checkData(FcSystem system) {
        if (systemDao.existSameCode(system.getId(), system.getCode())) {
            throw new FcDataDuplicatedException("The system code[{}] already exists.", system.getCode());
        }
    }

    private void fillWithAuth(List<FcSystem> systemList) {
        if (CollectionUtil.isEmpty(systemList)) {
            return;
        }
        List<Long> systemIds = systemList.stream()
                .map(FcSystem::getId)
                .collect(Collectors.toList());
        List<FcAuth> authList = authManager.listAllBySystemIds(systemIds);
        if (CollectionUtil.isEmpty(authList)) {
            return;
        }

        Map<Long, FcAuth> authMap = authList.stream()
                .collect(Collectors.toMap(FcAuth::getSysId, Function.identity()));

        for (FcSystem system : systemList) {
            FcAuth auth = authMap.get(system.getId());
            system.setAuth(auth);
        }
    }

    private void fillWithRetry(List<FcSystem> systemList) {
        if (CollectionUtil.isEmpty(systemList)) {
            return;
        }
        List<Long> systemIds = systemList.stream()
                .map(FcSystem::getId)
                .collect(Collectors.toList());
        List<FcRetry> retryList = retryManager.listAllBySystemIds(systemIds);
        if (CollectionUtil.isEmpty(retryList)) {
            return;
        }

        Map<Long, FcRetry> retryMap = retryList.stream()
                .collect(Collectors.toMap(FcRetry::getSysId, Function.identity()));

        for (FcSystem system : systemList) {
            FcRetry retry = retryMap.get(system.getId());
            system.setRetry(retry);
        }
    }

}
