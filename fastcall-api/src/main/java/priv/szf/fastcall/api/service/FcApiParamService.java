package priv.szf.fastcall.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.api.model.dto.FcApiParamDTO;
import priv.szf.fastcall.api.model.mapping.FcApiParamMapping;
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.common.event.source.FcSourceEvent;
import priv.szf.fastcall.data.mapper.FcApiParamMapper;
import priv.szf.fastcall.data.entity.FcApiParam;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcApiParamService extends ServiceImpl<FcApiParamMapper, FcApiParam> {

    private final FcApiParamMapping apiParamMapping;

    private final FcSourceEventPublisher sourceEventPublisher;

    public List<FcApiParamVO> listAllByApiId(Long apiId) {
        LambdaQueryWrapper<FcApiParam> qw = Wrappers.<FcApiParam>lambdaQuery()
                .eq(FcApiParam::getApiId, apiId);
        List<FcApiParam> list = super.list(qw);
        return apiParamMapping.toVoList(list);
    }

    public List<FcApiParamVO> saveOrUpdate(Long apiId, List<FcApiParamDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            removeByApiIds(Collections.singletonList(apiId));
            return Collections.emptyList();
        }

        removeExtraData(dtoList);

        List<FcApiParam> apiParamList = apiParamMapping.toEntityList(dtoList);
        super.saveOrUpdateBatch(apiParamList);

        sourceEventPublisher.publish(new FcSourceEvent<FcApiParam>(apiId));

        return listAllByApiId(apiId);
    }

    public void removeByApiIds(List<Long> apiIds) {
        if (CollectionUtils.isEmpty(apiIds)) {
            return;
        }
        LambdaQueryWrapper<FcApiParam> qw = Wrappers.<FcApiParam>lambdaQuery()
                .in(FcApiParam::getApiId, apiIds);
        super.remove(qw);
    }

    private void removeExtraData(List<FcApiParamDTO> dtoList) {
        Set<Long> apiIdSet = dtoList.stream()
                .filter(Objects::nonNull)
                .map(FcApiParamDTO::getId).collect(Collectors.toSet());
        LambdaQueryWrapper<FcApiParam> qw = Wrappers.<FcApiParam>lambdaQuery()
                .notIn(FcApiParam::getId, apiIdSet);
        super.remove(qw);
    }

}
