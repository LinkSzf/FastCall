package io.github.linkszf.fastcall.data.manager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.linkszf.fastcall.data.entity.FcApiParam;
import io.github.linkszf.fastcall.data.manager.support.FcShrinkSupport;
import io.github.linkszf.fastcall.data.mapper.FcApiDao;
import io.github.linkszf.fastcall.data.mapper.FcApiParamDao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class FcApiParamManager {

    private final FcApiDao apiDao;

    private final FcApiParamDao apiParamDao;

    public List<FcApiParam> listAllByApiId(Long apiId) {
        if (Objects.isNull(apiId)) {
            return Collections.emptyList();
        }
        return apiParamDao.listByApiId(apiId);
    }

    public List<FcApiParam> save(@NonNull Long apiId, List<FcApiParam> apiParamList) {
        if (!apiDao.exists(apiId)) {
            return Collections.emptyList();
        }

        if (CollectionUtil.isEmpty(apiParamList)) {
            removeByApiIds(Collections.singletonList(apiId));
            return Collections.emptyList();
        }

        FcShrinkSupport.assignParentId(apiParamList, apiId, FcApiParam::setApiId);

        FcShrinkSupport.shrinkToIncoming(
                apiParamDao.listByApiId(apiId),
                FcApiParam::getId,
                apiParamList,
                FcApiParam::getId,
                apiParamDao::removeBatchByIds
        );

        return apiParamDao.saveBatch(apiParamList);
    }

    public void removeByApiIds(List<Long> apiIds) {
        if (CollectionUtil.isEmpty(apiIds)) {
            return;
        }

        apiParamDao.removeBatchByApiIds(apiIds);
    }

}
