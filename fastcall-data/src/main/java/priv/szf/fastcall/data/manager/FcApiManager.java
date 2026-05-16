package priv.szf.fastcall.data.manager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.common.exception.FcDataDuplicatedException;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.manager.support.FcShrinkSupport;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class FcApiManager {

    private final FcApiDao apiDao;

    private final FcSystemDao systemDao;

    private final FcApiParamManager apiParamManager;

    public List<FcApi> listAllBySystemId(Long systemId) {
        if (Objects.isNull(systemId)) {
            return Collections.emptyList();
        }
        return apiDao.listBySystemId(systemId);
    }

    public List<FcApi> save(@NonNull Long systemId, List<FcApi> apiList) {
        if (!systemDao.exists(systemId)) {
            return Collections.emptyList();
        }

        if (CollectionUtils.isEmpty(apiList)) {
            removeBySystemId(systemId);
            return Collections.emptyList();
        }

        FcShrinkSupport.assignParentId(apiList, systemId, FcApi::setSysId);

        checkData(apiList);

        FcShrinkSupport.shrinkToIncoming(
                apiDao.listBySystemId(systemId),
                FcApi::getId,
                apiList,
                FcApi::getId,
                removeIds -> {
                    apiParamManager.removeByApiIds(removeIds);
                    apiDao.removeBatchByIds(removeIds);
                }
        );

        return apiDao.saveBatch(apiList);
    }

    public void removeBySystemId(Long systemId) {
        if (Objects.isNull(systemId)) {
            return;
        }

        List<FcApi> apiList = apiDao.listBySystemId(systemId);
        if (CollectionUtils.isEmpty(apiList)) {
            return;
        }
        List<Long> apiIds = apiList.stream().map(FcApi::getId).distinct().collect(Collectors.toList());
        apiParamManager.removeByApiIds(apiIds);
        apiDao.removeBatchByIds(apiIds);
    }

    private void checkData(List<FcApi> apiList) {
        Set<String> nameSet = new HashSet<>();
        Set<String> duplicateNames = new HashSet<>();

        for (FcApi api : apiList) {
            if (api == null) {
                continue;
            }
            String name = api.getName();
            if (!nameSet.add(name)) {
                duplicateNames.add(name);
            }
        }

        if (!duplicateNames.isEmpty()) {
            throw new FcDataDuplicatedException("Api name cannot be duplicated with [{}].", String.join(", ", duplicateNames));
        }
    }

}
