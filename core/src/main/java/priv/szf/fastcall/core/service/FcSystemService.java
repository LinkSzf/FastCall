package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.model.dto.FcSystemDTO;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.vo.FcSystemVO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcSystemService extends ServiceImpl<FcSystemMapper, FcSystem> {

    private final FcSystemMapper systemMapper;

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
        FcSystem fcSystem = super.getById(id);
        FcSystemVO vo = new FcSystemVO();
        BeanUtils.copyProperties(fcSystem, vo);
        return vo;
    }

    public void saveOrUpdate(FcSystemDTO dto) {
        if (dto.getId() == null) {
            checkData(dto);
        }

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
        QueryWrapper<FcSystem> codeQuery = new QueryWrapper<FcSystem>().eq("code", dto.getCode());
        long count = super.count(codeQuery);
        if (count > 0) {
            throw new RuntimeException("该系统编码已存在");
        }
    }
}
