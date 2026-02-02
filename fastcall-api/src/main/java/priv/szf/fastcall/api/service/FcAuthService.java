package priv.szf.fastcall.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.api.model.mapping.FcAuthMapping;
import priv.szf.fastcall.api.model.vo.FcAuthVO;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.mapper.FcAuthDao;

import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class FcAuthService {

    private final FcAuthDao authDao;

    private final FcAuthMapping authMapping;

    public FcAuthVO getBySystemId(Long systemId) {
        FcAuth auth = authDao.getBySystemId(systemId);
        return authMapping.toVo(auth);
    }

    public FcAuthVO save(FcAuth auth, Long systemId) {
        if (Objects.isNull(auth)) {
            removeBySystemId(systemId);
            return null;
        }

        if (Objects.isNull(auth.getId())) {
            removeBySystemId(systemId);
        }

        auth.setSysId(systemId);
        FcAuth savedAuth = authDao.insertOrUpdate(auth);

        return authMapping.toVo(savedAuth);
    }

    public void removeBySystemId(Long systemId) {
        FcAuthVO authVO = getBySystemId(systemId);

        if (Objects.isNull(authVO)) {
            return;
        }

        Long authId = Long.parseLong(authVO.getId());
        authDao.removeById(authId);
    }

}
