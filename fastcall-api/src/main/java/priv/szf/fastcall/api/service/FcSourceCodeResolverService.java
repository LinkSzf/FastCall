package priv.szf.fastcall.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcSourceCodeResolverService implements IFcSourceCodeResolver {

    private final FcSystemDao systemDao;

    private final FcApiDao apiDao;

    @Override
    public String getSystemCodeBySystemId(Long systemId) {
        FcSystem system = systemDao.getOneById(systemId);
        return Optional.ofNullable(system)
                .map(FcSystem::getCode)
                .orElse(null);
    }

    @Override
    public String getSystemCodeByApiId(Long apiId) {
        FcApi api = apiDao.getOneById(apiId);
        return Optional.ofNullable(api)
                .map(FcApi::getSysId)
                .map(this::getSystemCodeBySystemId)
                .orElse(null);
    }
}

