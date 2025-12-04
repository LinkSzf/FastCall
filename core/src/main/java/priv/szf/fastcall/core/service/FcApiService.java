package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.model.dto.FcApiDTO;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.vo.FcApiVO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcApiService extends ServiceImpl<FcApiMapper, FcApi> {

    public List<FcApiVO> listAllBySystemId(Long systemId) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId);
        List<FcApi> list = super.list(qw);
        return list.stream()
                .map(i -> {
                    FcApiVO vo = new FcApiVO();
                    BeanUtils.copyProperties(i, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public void saveOrUpdate(Long systemId, List<FcApiDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            removeBySystemId(systemId);
            return;
        }

        List<FcApi> apiList = dtoList.stream()
                .map(i -> {
                    FcApi api = new FcApi();
                    BeanUtils.copyProperties(i, api);
                    return api;
                })
                .collect(Collectors.toList());
        super.saveOrUpdateBatch(apiList);
    }

    private void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId);
        super.remove(qw);
    }
}
