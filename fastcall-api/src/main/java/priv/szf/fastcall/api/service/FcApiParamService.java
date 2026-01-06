package priv.szf.fastcall.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.api.model.dto.FcApiParamDTO;
import priv.szf.fastcall.api.model.mapping.FcApiParamMapping;
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.entity.FcApiParam;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
        if (CollectionUtils.isEmpty(dtoList)) {
            removeByApiIds(Collections.singletonList(apiId));
            return Collections.emptyList();
        }

        shrinkApiParamToThis(dtoList);

        List<FcApiParam> apiParamList = apiParamMapping.toEntityList(dtoList);
        List<FcApiParam> savedApiParamList = apiParamDao.insertOrUpdateBatch(apiParamList);

        return apiParamMapping.toVoList(savedApiParamList);
    }

    public void removeByApiIds(List<Long> apiIds) {
        if (CollectionUtils.isEmpty(apiIds)) {
            return;
        }

        apiParamDao.removeBatchByApiIds(apiIds);
    }

    private void shrinkApiParamToThis(List<FcApiParamDTO> dtoList) {
        List<Long> apiIdList = dtoList.stream()
                .filter(Objects::nonNull)
                .map(FcApiParamDTO::getId)
                .distinct()
                .collect(Collectors.toList());
        apiParamDao.removeBatchNotInIds(apiIdList);
    }

}
