package priv.szf.fastcall.data.manager;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.manager.support.FcShrinkSupport;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class FcHeaderAssignManager {

    private final FcHeaderAssignDao headerAssignDao;

    private final FcSystemDao systemDao;

    public List<FcHeaderAssign> listAllHeaderAssignBySystemId(Long systemId) {
        if (Objects.isNull(systemId)) {
            return Collections.emptyList();
        }
        return headerAssignDao.listBySystemId(systemId);
    }

    public List<FcHeaderAssign> save(@NonNull Long systemId, List<FcHeaderAssign> headerAssignList) {
        if (systemDao.exists(systemId)) {
            return Collections.emptyList();
        }

        if (CollectionUtil.isEmpty(headerAssignList)) {
            removeBySystemId(systemId);
            return Collections.emptyList();
        }

        FcShrinkSupport.assignParentId(headerAssignList, systemId, FcHeaderAssign::setSysId);

        FcShrinkSupport.shrinkToIncoming(
                headerAssignDao.listBySystemId(systemId),
                FcHeaderAssign::getId,
                headerAssignList,
                FcHeaderAssign::getId,
                headerAssignDao::removeBatchByIds
        );

        return headerAssignDao.saveBatch(headerAssignList);
    }

    private void removeBySystemId(Long systemId) {
        if (Objects.nonNull(systemId)) {
            headerAssignDao.removeBatchBySystemId(systemId);
        }
    }
}
