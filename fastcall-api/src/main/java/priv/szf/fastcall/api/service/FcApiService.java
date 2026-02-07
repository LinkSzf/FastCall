package priv.szf.fastcall.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.api.model.dto.FcApiDTO;
import priv.szf.fastcall.api.model.mapping.FcApiMapping;
import priv.szf.fastcall.api.model.vo.FcApiVO;
import priv.szf.fastcall.common.exception.FcDataDuplicatedException;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.entity.FcApi;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class FcApiService {

    private final FcApiDao apiDao;

    private final FcApiMapping apiMapping;

    private final FcApiParamService apiParamService;

    public List<FcApiVO> listAllBySystemId(Long systemId) {
        List<FcApi> list = apiDao.listBySystemId(systemId);
        return apiMapping.toVoList(list);
    }

    public List<FcApiVO> save(Long systemId, List<FcApiDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            removeBySystemId(systemId);
            return Collections.emptyList();
        }

        dtoList.stream()
                .filter(Objects::nonNull)
                .forEach(dto -> dto.setSysId(systemId));

        checkData(dtoList);

        shrinkApisToThis(systemId, dtoList);

        List<FcApi> apiList = apiMapping.toEntityList(dtoList);
        List<FcApi> savedApiList = apiDao.insertOrUpdateBatch(apiList);

        return apiMapping.toVoList(savedApiList);
    }

    public void removeBySystemId(Long systemId) {
        List<FcApi> apiList = apiDao.listBySystemId(systemId);
        List<Long> apiIds = apiList.stream().map(FcApi::getId).distinct().collect(Collectors.toList());
        apiParamService.removeByApiIds(apiIds);
        apiDao.removeBatchByIds(apiIds);
    }

    private void checkData(List<FcApiDTO> dtoList) {
        Set<String> nameSet = new HashSet<>();
        Set<String> duplicateNames = new HashSet<>();

        for (FcApiDTO api : dtoList) {
            String name = api.getName();
            if (!nameSet.add(name)) {
                duplicateNames.add(name);
            }
        }

        if (!duplicateNames.isEmpty()) {
            throw new FcDataDuplicatedException("Api name cannot be duplicated with [{}].", String.join(", ", duplicateNames));
        }
    }

    private void shrinkApisToThis(Long systemId, List<FcApiDTO> dtoList) {
        List<FcApi> existingApis = apiDao.listBySystemId(systemId);
        if (CollectionUtils.isEmpty(existingApis)) {
            return;
        }

        Set<Long> keepIds = dtoList.stream()
                .filter(Objects::nonNull)
                .map(FcApiDTO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toSet());

        List<Long> removeIds = existingApis.stream()
                .map(FcApi::getId)
                .filter(Objects::nonNull)
                .filter(id -> !keepIds.contains(id))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(removeIds)) {
            return;
        }

        apiParamService.removeByApiIds(removeIds);
        apiDao.removeBatchByIds(removeIds);
    }

}
