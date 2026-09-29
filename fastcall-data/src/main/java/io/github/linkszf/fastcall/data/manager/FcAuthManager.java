package io.github.linkszf.fastcall.data.manager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.linkszf.fastcall.data.entity.FcAuth;
import io.github.linkszf.fastcall.data.mapper.FcAuthDao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class FcAuthManager {

    private final FcAuthDao authDao;

    public List<FcAuth> listAllBySystemIds(Collection<Long> systemIds) {
        if (CollectionUtil.isEmpty(systemIds)) {
            return Collections.emptyList();
        }
        return authDao.listBySystemIds(systemIds);
    }

    public FcAuth getBySystemId(Long systemId) {
        if (Objects.isNull(systemId)) {
            return null;
        }
        return authDao.getBySystemId(systemId);
    }

    public FcAuth save(@NonNull Long systemId, @NonNull FcAuth auth) {
        if (Objects.isNull(auth.getId())) {
            Optional.ofNullable(getBySystemId(systemId))
                    .ifPresent(existing -> auth.setId(existing.getId()));
        }

        auth.setSysId(systemId);
        return authDao.saveOne(auth);
    }

    public void removeBySystemId(Long systemId) {
        if (Objects.nonNull(systemId)) {
            authDao.removeBySystemId(systemId);
        }
    }
}
