package priv.szf.fastcall.api.service;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.api.model.dto.FcApiParamDTO;
import priv.szf.fastcall.api.model.mapping.FcApiParamMapping;
import priv.szf.fastcall.api.service.support.FcShrinkSupport;
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.entity.FcApiParam;

import java.util.Collections;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class FcApiParamService {

    private final FcApiParamDao apiParamDao;

    private final FcApiParamMapping apiParamMapping;

    public List<FcApiParamVO> listAllByApiId(Long apiId) {
        List<FcApiParam> list = apiParamDao.listByApiId(apiId);
        return apiParamMapping.toVoList(list);
    }

    public List<FcApiParamVO> save(Long apiId, List<FcApiParamDTO> dtoList) {
        if (CollectionUtil.isEmpty(dtoList)) {
            removeByApiIds(Collections.singletonList(apiId));
            return Collections.emptyList();
        }

        FcShrinkSupport.assignParentId(dtoList, apiId, FcApiParamDTO::setApiId);

        FcShrinkSupport.shrinkToIncoming(
                apiParamDao.listByApiId(apiId),
                FcApiParam::getId,
                dtoList,
                FcApiParamDTO::getId,
                apiParamDao::removeBatchByIds
        );

        List<FcApiParam> apiParamList = apiParamMapping.toEntityList(dtoList);
        List<FcApiParam> savedApiParamList = apiParamDao.saveBatch(apiParamList);

        return apiParamMapping.toVoList(savedApiParamList);
    }

    public void removeByApiIds(List<Long> apiIds) {
        if (CollectionUtil.isEmpty(apiIds)) {
            return;
        }

        apiParamDao.removeBatchByApiIds(apiIds);
    }

}
