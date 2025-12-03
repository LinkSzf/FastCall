package priv.szf.fastcall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.model.dto.FcAuthDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcAuthService extends ServiceImpl<FcAuthMapper, FcAuth> {

    private final FcAuthMapper authMapper;

    public void saveOrUpdate(FcAuthDTO dto, Long systemId) {
        if (dto == null) {
            removeBySystemId(systemId);
            return;
        }

        FcAuth auth = new FcAuth();
        BeanUtils.copyProperties(dto, auth);
        super.saveOrUpdate(auth);
    }

    public void removeBySystemId(Long systemId) {
        QueryWrapper<FcAuth> queryWrapper = new QueryWrapper<FcAuth>().eq("sys_id", systemId);
        super.remove(queryWrapper);
    }

}
