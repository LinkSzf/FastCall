package priv.szf.fastcall.data.manager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.common.exception.FcDataDuplicatedException;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.time.LocalDateTime;
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

    private final FcApiManager apiManager;

    private final FcSystemDao systemDao;

    public List<FcSystem> listAll() {
        return systemDao.list();
    }

    public List<FcSystem> listAllWithAuth() {
        List<FcSystem> systemList = listAll();
        List<Long> systemIds = systemList.stream()
                .map(FcSystem::getId)
                .collect(Collectors.toList());
        List<FcAuth> authList = authManager.listAllBySystemIds(systemIds);
        if (CollectionUtil.isEmpty(authList)) {
            return systemList;
        }

        Map<Long, FcAuth> authMap = authList.stream()
                .collect(Collectors.toMap(FcAuth::getSysId, Function.identity()));

        for (FcSystem system : systemList) {
            FcAuth auth = authMap.get(system.getId());
            system.setAuth(auth);
        }
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
                    return system;
                })
                .orElse(null);
    }

    public FcSystem save(@NonNull FcSystem system) {
        checkData(system);

        system.setUpdateAt(LocalDateTime.now());
        FcSystem savedSystem = systemDao.saveOne(system);

        Long systemId = savedSystem.getId();
        FcAuth auth = system.getAuth();
        authManager.save(systemId, auth);

        return system;
    }

    public void removeById(Long id) {
        if (Objects.isNull(id)) {
            return;
        }
        authManager.removeBySystemId(id);
        apiManager.removeBySystemId(id);
        systemDao.removeById(id);
    }

    private void checkData(FcSystem system) {
        if (systemDao.existSameCode(system.getId(), system.getCode())) {
            throw new FcDataDuplicatedException("The system code[{}] already exists.", system.getCode());
        }
    }
}
