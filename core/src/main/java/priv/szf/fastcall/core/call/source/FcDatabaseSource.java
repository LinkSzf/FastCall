package priv.szf.fastcall.core.call.source;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.mapping.FcPakMapping;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Component
@RequiredArgsConstructor
public class FcDatabaseSource implements IFcSource {

    private final FcSystemMapper systemMapper;

    private final FcAuthMapper authMapper;

    private final FcApiMapper apiMapper;

    private final FcPakMapping pakMapping;

    @Override
    public FcSourcePak getSourcePak(String systemCode) {
        LambdaQueryWrapper<FcSystem> systemQw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, systemCode);
        FcSystem system = systemMapper.selectOne(systemQw);
        if (Objects.isNull(system)) {
            throw new FcBizException("未找到该系统信息");
        }

        FcSystemPak systemPak = pakMapping.toSystemPak(system);

        Long systemId = system.getId();
        FcAuthPak auth = getAuthBySysId(systemId);

//        List<FcApiPak> apis = getApisBySystemId(systemId);
        Map<String, FcApiPak> apis = getApisBySysId(systemId);

        return new FcSourcePak(systemPak, auth, apis);
    }

    @Override
    public String getAccessToken(String systemCode) {
        // TODO: 获取系统访问令牌
        return "";
    }

    private FcApiPak getApiBySysIdAndApiName(Long systemId, String apiName) {
        LambdaQueryWrapper<FcApi> apiQw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId)
                .eq(FcApi::getName, apiName);
        FcApi api = apiMapper.selectOne(apiQw);
        return pakMapping.toApiPak(api);
    }

    private FcAuthPak getAuthBySysId(Long sysId) {
        LambdaQueryWrapper<FcAuth> authQw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, sysId);
        FcAuth auth = authMapper.selectOne(authQw);
        if (Objects.isNull(auth)) {
            throw new FcBizException("未找到该系统认证配置信息");
        }

        return pakMapping.toAuthPak(auth);
    }

    private Map<String, FcApiPak> getApisBySysId(Long sysId) {
        LambdaQueryWrapper<FcApi> apiQw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, sysId);
        return apiMapper.selectList(apiQw)
                .stream()
                .map(pakMapping::toApiPak)
                .collect(Collectors.toMap(FcApiPak::getName, Function.identity()));
    }

    private List<FcApiPak> getApisBySystemId(Long sysId) {
        LambdaQueryWrapper<FcApi> apiQw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, sysId);
        return apiMapper.selectList(apiQw)
                .stream()
                .map(pakMapping::toApiPak)
                .collect(Collectors.toList());
    }

}
