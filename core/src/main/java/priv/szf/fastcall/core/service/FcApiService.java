package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.core.common.exception.FastCallException;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.model.dto.FcApiDTO;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.mapping.FcApiMapping;
import priv.szf.fastcall.core.model.vo.FcApiVO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcApiService extends ServiceImpl<FcApiMapper, FcApi> {

    private final FcApiMapping apiMapping;

    private final FcApiParamService apiParamService;

    public List<FcApiVO> listAllBySystemId(Long systemId) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId);
        List<FcApi> list = super.list(qw);
        return apiMapping.toVoList(list);
    }

    public List<FcApiVO> saveOrUpdate(Long systemId, List<FcApiDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            removeBySystemId(systemId);
            return Collections.emptyList();
        }

        checkData(dtoList);

        removeExtraData(dtoList);

        List<FcApi> apiList = apiMapping.toEntityList(dtoList);
        super.saveOrUpdateBatch(apiList);

        return listAllBySystemId(systemId);
    }

    public void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId);
        List<FcApi> apiList = super.list(qw);
        List<Long> apiIds = apiList.stream().map(FcApi::getId).collect(Collectors.toList());
        apiParamService.removeByApiIds(apiIds);
        super.removeBatchByIds(apiIds);
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
            throw new FastCallException(
                    "发现重复的名称: " + String.join(", ", duplicateNames)
            );
        }
    }

    private void removeExtraData(List<FcApiDTO> dtoList) {
        Set<Long> apiIdSet = dtoList.stream()
                .filter(Objects::nonNull)
                .map(FcApiDTO::getId).collect(Collectors.toSet());
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .notIn(FcApi::getId, apiIdSet);
        super.remove(qw);
    }

}
