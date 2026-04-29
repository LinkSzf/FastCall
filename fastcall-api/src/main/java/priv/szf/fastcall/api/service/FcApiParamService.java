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
import java.util.Objects;

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

        dtoList.stream()
                .filter(Objects::nonNull)
                .forEach(dto -> dto.setApiId(apiId));

        shrinkApiParamToThis(apiId, dtoList);

        List<FcApiParam> apiParamList = apiParamMapping.toEntityList(dtoList);
        List<FcApiParam> savedApiParamList = apiParamDao.insertOrUpdateBatch(apiParamList);

        return apiParamMapping.toVoList(savedApiParamList);
    }

    public void removeByApiIds(List<Long> apiIds) {
        if (CollectionUtil.isEmpty(apiIds)) {
            return;
        }

        apiParamDao.removeBatchByApiIds(apiIds);
    }

    private void shrinkApiParamToThis(Long apiId, List<FcApiParamDTO> dtoList) {
        List<FcApiParam> existingParams = apiParamDao.listByApiId(apiId);
        List<Long> removeIds = FcShrinkSupport.resolveRemoveIds(
                existingParams,
                FcApiParam::getId,
                dtoList,
                FcApiParamDTO::getId
        );

        if (CollectionUtil.isEmpty(removeIds)) {
            return;
        }

        apiParamDao.removeBatchByIds(removeIds);
    }

}
