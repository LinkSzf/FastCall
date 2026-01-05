package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.event.source.FcSourceEventPublisher;
import priv.szf.fastcall.core.event.source.FcSourceEvent;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;

import java.util.Objects;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcAuthService extends ServiceImpl<FcAuthMapper, FcAuth> {

    private final FcSourceEventPublisher sourceEventPublisher;

    public FcAuth getBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        return super.getOne(qw);
    }

    public void saveOrUpdate(FcAuth auth, Long systemId) {
        if (Objects.isNull(auth)) {
            removeBySystemId(systemId);
            return;
        }

        if (Objects.isNull(auth.getId())) {
            removeBySystemId(systemId);
        }

        auth.setSysId(systemId);
        super.saveOrUpdate(auth);

        sourceEventPublisher.publish(new FcSourceEvent<FcSystem>(systemId));
    }

    public void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        super.remove(qw);
        sourceEventPublisher.publish(new FcSourceEvent<FcSystem>(systemId));
    }

}
