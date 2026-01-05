package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.common.exception.FcDataDuplicatedException;
import priv.szf.fastcall.core.common.exception.FcDataNotFoundException;
import priv.szf.fastcall.core.event.source.FcSourceEventPublisher;
import priv.szf.fastcall.core.event.source.FcSourceEvent;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.model.dto.FcSystemDTO;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.mapping.FcSystemMapping;
import priv.szf.fastcall.core.model.vo.FcSystemVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcSystemService extends ServiceImpl<FcSystemMapper, FcSystem> {

    private final FcAuthService authService;

    private final FcApiService apiService;

    private final FcSystemMapping systemMapping;

    private final FcSourceEventPublisher sourceEventPublisher;

    public List<FcSystemVO> listAll() {
        List<FcSystem> list = super.list();
        return systemMapping.toVoList(list);
    }

    public FcSystemVO getById(Long id) {
        return Optional.ofNullable(super.getById(id)).map(i -> {
                    i.setAuth(authService.getBySystemId(i.getId()));
                    return systemMapping.toVo(i);
                })
                .orElseThrow(() -> new FcDataNotFoundException("系统[%s]不存在",  id));
    }

    public FcSystemVO saveOrUpdate(FcSystemDTO dto) {
        checkData(dto);

        FcSystem system = systemMapping.toEntity(dto);
        system.setUpdateAt(LocalDateTime.now());
        super.saveOrUpdate(system);

        Long systemId = system.getId();
        authService.saveOrUpdate(system.getAuth(), systemId);

        sourceEventPublisher.publish(new FcSourceEvent<FcSystem>(systemId));

        return systemMapping.toVo(system);
    }

    public void removeById(Long id) {
        authService.removeBySystemId(id);
        apiService.removeBySystemId(id);
        super.removeById(id);
        sourceEventPublisher.publish(new FcSourceEvent<FcSystem>(id));
    }

    private void checkData(FcSystemDTO dto) {
        LambdaQueryWrapper<FcSystem> qw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, dto.getCode());
        qw = Objects.isNull(dto.getId()) ? qw : qw.ne(FcSystem::getId, dto.getId());
        long count = super.count(qw);
        if (count > 0) {
            throw new FcDataDuplicatedException("系统编码[%s]已存在", dto.getCode());
        }
    }
}
