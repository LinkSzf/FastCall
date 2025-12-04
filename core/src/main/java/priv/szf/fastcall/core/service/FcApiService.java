package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.model.dto.FcApiDTO;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.mapping.FcApiMapping;
import priv.szf.fastcall.core.model.vo.FcApiVO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcApiService extends ServiceImpl<FcApiMapper, FcApi> {

    private final FcApiMapping apiMapping;

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

        List<FcApi> apiList = apiMapping.toEntityList(dtoList);
        super.saveOrUpdateBatch(apiList);

        return apiMapping.toVoList(apiList);
    }

    public void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId);
        super.remove(qw);
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
            throw new FcBizException(
                    "发现重复的名称: " + String.join(", ", duplicateNames)
            );
        }
    }

}
