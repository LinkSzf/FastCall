package priv.szf.fastcall.core.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.model.dto.FcAuthDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;

import java.util.Objects;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcAuthService extends ServiceImpl<FcAuthMapper, FcAuth> {

    public FcAuth getBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        //                .map(i -> {
        //                    FcAuthVO vo = new FcAuthVO();
        //                    BeanUtils.copyProperties(i, vo);
        //                    vo.setContent(JSON.parseObject(i.getContent(), i.getType().getClazz()));
        //                    return vo;
        //                })
        return super.getOne(qw);
    }

    public void saveOrUpdate(FcAuthDTO dto, Long systemId) {
        if (Objects.isNull(dto)) {
            removeBySystemId(systemId);
            return;
        }

        if (Objects.isNull(dto.getId())) {

        }

        FcAuth auth = new FcAuth();
        BeanUtils.copyProperties(dto, auth);
        auth.setContent(JSON.toJSONString(dto.getContent()));
        super.saveOrUpdate(auth);
    }

    public void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        super.remove(qw);
    }

}
