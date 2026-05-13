package priv.szf.fastcall.api.service;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.api.model.dto.FcHeaderAssignDTO;
import priv.szf.fastcall.api.model.mapping.FcHeaderAssignMapping;
import priv.szf.fastcall.api.service.support.FcShrinkSupport;
import priv.szf.fastcall.api.model.vo.FcHeaderAssignVO;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;

import java.util.Collections;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class FcHeaderAssignService {

    private final FcHeaderAssignDao headerAssignDao;

    private final FcHeaderAssignMapping  headerAssignMapping;


    public List<FcHeaderAssignVO> listAllHeaderAssignBySystemId(Long systemId) {
        List<FcHeaderAssign> headerAssignList = headerAssignDao.listBySystemId(systemId);
        return headerAssignMapping.toVoList(headerAssignList);
    }

    public List<FcHeaderAssignVO> save(Long systemId, List<FcHeaderAssignDTO> dtoList) {

        if (CollectionUtil.isEmpty(dtoList)) {
            removeBySystemId(systemId);
            return Collections.emptyList();
        }

        FcShrinkSupport.assignParentId(dtoList, systemId, FcHeaderAssignDTO::setSysId);

        FcShrinkSupport.shrinkToIncoming(
                headerAssignDao.listBySystemId(systemId),
                FcHeaderAssign::getId,
                dtoList,
                FcHeaderAssignDTO::getId,
                headerAssignDao::removeBatchByIds
        );

        List<FcHeaderAssign> apiParamList = headerAssignMapping.toEntityList(dtoList);
        List<FcHeaderAssign> savedApiParamList = headerAssignDao.saveBatch(apiParamList);

        return headerAssignMapping.toVoList(savedApiParamList);

    }

    private void removeBySystemId(Long systemId) {
        headerAssignDao.removeBatchBySystemId(systemId);
    }
}
