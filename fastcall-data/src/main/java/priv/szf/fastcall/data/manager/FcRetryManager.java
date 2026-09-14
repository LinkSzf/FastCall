package priv.szf.fastcall.data.manager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.data.entity.FcRetry;
import priv.szf.fastcall.data.mapper.FcRetryDao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class FcRetryManager {

    private final FcRetryDao retryDao;

    public List<FcRetry> listAllBySystemIds(Collection<Long> systemIds) {
        if (CollectionUtil.isEmpty(systemIds)) {
            return Collections.emptyList();
        }
        return retryDao.listBySystemIds(systemIds);
    }

    public FcRetry getBySystemId(Long systemId) {
        if (Objects.isNull(systemId)) {
            return null;
        }
        return retryDao.getBySystemId(systemId);
    }

    public FcRetry save(@NonNull Long systemId, FcRetry retry) {
        if (Objects.isNull(retry)
                || Objects.isNull(retry.getAttempts())
                || retry.getAttempts() <= 0) {
            removeBySystemId(systemId);
            return null;
        }

        if (Objects.isNull(retry.getId())) {
            Optional.ofNullable(getBySystemId(systemId))
                    .ifPresent(existing -> retry.setId(existing.getId()));
        }

        retry.setSysId(systemId);
        return retryDao.saveOne(retry);
    }

    public void removeBySystemId(Long systemId) {
        if (Objects.nonNull(systemId)) {
            retryDao.removeBySystemId(systemId);
        }
    }
}
