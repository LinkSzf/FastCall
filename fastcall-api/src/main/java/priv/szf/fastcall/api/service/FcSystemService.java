package priv.szf.fastcall.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.api.model.dto.FcSystemDTO;
import priv.szf.fastcall.api.model.mapping.FcAuthMapping;
import priv.szf.fastcall.api.model.mapping.FcSystemMapping;
import priv.szf.fastcall.api.model.vo.FcAuthVO;
import priv.szf.fastcall.api.model.vo.FcSystemVO;
import priv.szf.fastcall.common.event.source.FcSourceEvent;
import priv.szf.fastcall.common.exception.FcDataDuplicatedException;
import priv.szf.fastcall.common.exception.FcDataNotFoundException;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.mapper.FcSystemDao;
import priv.szf.fastcall.data.entity.FcSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FcSystemService {

    private final FcAuthService authService;

    private final FcApiService apiService;

    private final FcSystemMapping systemMapping;

    private final FcAuthMapping authMapping;

    private final FcSourceEventPublisher sourceEventPublisher;

    private final FcSystemDao systemDao;

    public List<FcSystemVO> listAll() {
        List<FcSystem> list = systemDao.list();
        return systemMapping.toVoList(list);
    }

    public FcSystemVO getById(Long id) {
        return Optional.ofNullable(systemDao.getOneById(id)).map(i -> {
                    FcAuthVO auth = authService.getBySystemId(i.getId());
                    FcSystemVO system = systemMapping.toVo(i);
                    system.setAuth(auth);
                    return system;
                })
                .orElseThrow(() -> new FcDataNotFoundException("系统[%s]不存在",  id));
    }

    public FcSystemVO save(FcSystemDTO dto) {
        checkData(dto);

        FcSystem system = systemMapping.toEntity(dto);
        system.setUpdateAt(LocalDateTime.now());
        FcSystem savedSystem = systemDao.insertOrUpdate(system);

        FcAuth auth = authMapping.toEntity(dto.getAuth());
        Long systemId = savedSystem.getId();
        FcAuthVO savedAuth = authService.save(auth, systemId);

        sourceEventPublisher.publish(new FcSourceEvent<FcSystem>(systemId));

        FcSystemVO systemVO = systemMapping.toVo(savedSystem);
        systemVO.setAuth(savedAuth);
        return systemVO;
    }

    public void removeById(Long id) {
        authService.removeBySystemId(id);
        apiService.removeBySystemId(id);
        systemDao.removeById(id);
        sourceEventPublisher.publish(new FcSourceEvent<FcSystem>(id));
    }

    private void checkData(FcSystemDTO dto) {
        if (systemDao.existSameCode(dto.getId(), dto.getCode())) {
            throw new FcDataDuplicatedException("系统编码[%s]已存在", dto.getCode());
        }
    }
}
