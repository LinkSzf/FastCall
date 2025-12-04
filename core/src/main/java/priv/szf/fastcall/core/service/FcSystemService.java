package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.common.FcDataDuplicatedException;
import priv.szf.fastcall.core.common.FcDataNotFoundException;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.model.dto.FcSystemDTO;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.vo.FcSystemVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcSystemService extends ServiceImpl<FcSystemMapper, FcSystem> {

    private final FcAuthService authService;

    public List<FcSystemVO> listAll() {
        List<FcSystem> list = super.list();
        List<FcSystemVO> voList = new ArrayList<>();
        for (FcSystem fcSystem : list) {
            FcSystemVO vo = new FcSystemVO();
            BeanUtils.copyProperties(fcSystem, vo);
            voList.add(vo);
        }
        return voList;
    }

    public FcSystemVO getById(Long id) {
        return Optional.ofNullable(super.getById(id)).map(i -> {
                    i.setAuth(authService.getBySystemId(i.getId()));
//                    return systemMapping.toVo(i);
                    return new FcSystemVO();
                })
                .orElseThrow(FcDataNotFoundException::new);
    }

    public void saveOrUpdate(FcSystemDTO dto) {
        checkData(dto);

        FcSystem system = new FcSystem();
        BeanUtils.copyProperties(dto, system);
        super.saveOrUpdate(system);

        authService.saveOrUpdate(dto.getAuth(), system.getId());
    }

    public void removeById(Long id) {
        authService.removeBySystemId(id);
        super.removeById(id);
    }

    private void checkData(FcSystemDTO dto) {
        LambdaQueryWrapper<FcSystem> qw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, dto.getCode());
        qw = Objects.isNull(dto.getId()) ? qw : qw.ne(FcSystem::getId, dto.getId());
        long count = super.count(qw);
        if (count > 0) {
            throw new FcDataDuplicatedException("该系统编码已存在");
        }
    }
}
