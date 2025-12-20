package priv.szf.fastcall.core.call.source;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.mapper.FcTokenMapper;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.entity.FcToken;
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

    private final FcTokenMapper tokenMapper;

    private final FcApiMapper apiMapper;

    private final FcPakMapping pakMapping;

    @Override
    public FcSourcePak getSourcePak(String systemCode) {
        FcSystem systemEntity = getSystemByCode(systemCode);

        FcSystemPak system = pakMapping.toSystemPak(systemEntity);

        Long systemId = systemEntity.getId();
        FcAuthPak auth = getAuthBySysId(systemId);

        FcTokenPak<?> token = getAccessTokenBySysId(systemId);

        Map<String, FcApiPak> apis = getApisBySysId(systemId);

        return new FcSourcePak(system, auth, token, apis);
    }

    private FcSystem getSystemByCode(String systemCode) {
        LambdaQueryWrapper<FcSystem> systemQw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, systemCode);
        FcSystem systemEntity = systemMapper.selectOne(systemQw);
        if (Objects.isNull(systemEntity)) {
            throw new FcBizException(String.format("FastCall-未找到该系统[%s]的注册信息", systemCode));
        }
        return systemEntity;
    }

    @Override
    public <T> FcTokenPak<T> getAccessToken(String systemCode) {
        return (FcTokenPak<T>) getSourcePak(systemCode).getAccessToken();
    }

    @Async
    @Override
    public <T> void updateAccessToken(String systemCode, FcTokenPak<T> token) {
        FcToken accessToken = pakMapping.toToken((FcTokenPak<String>) token);
        FcSystem system = getSystemByCode(systemCode);
        Long systemId = system.getId();
        accessToken.setSysId(systemId);
        tokenMapper.delete(new LambdaQueryWrapper<FcToken>().eq(FcToken::getSysId, systemId));
        tokenMapper.insert(accessToken);
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

    private FcTokenPak<?> getAccessTokenBySysId(Long systemId) {
        LambdaQueryWrapper<FcToken> tokenQw = Wrappers.<FcToken>lambdaQuery()
                .eq(FcToken::getSysId, systemId);
        FcToken accessToken = tokenMapper.selectOne(tokenQw);
        return pakMapping.toTokenPak(accessToken);
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
