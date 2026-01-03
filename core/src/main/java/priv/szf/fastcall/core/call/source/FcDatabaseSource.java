package priv.szf.fastcall.core.call.source;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.mapper.FcApiParamMapper;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcApiParamPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.auth.credential.ICredential;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcApiParam;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.mapping.FcPakMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Component
@RequiredArgsConstructor
public class FcDatabaseSource extends FcBaseChainSource implements IFcSource {

    private final FcSystemMapper systemMapper;

    private final FcAuthMapper authMapper;

    private final FcApiMapper apiMapper;

    private final FcPakMapping pakMapping;

    private final FcApiParamMapper apiParamMapper;

    @Override
    protected FcSourcePak tryGetSourcePak(String systemCode) {
        FcSystem systemEntity = getSystemByCode(systemCode);
        if (Objects.isNull(systemEntity)) {
            return null;
        }

        FcSystemPak system = pakMapping.toSystemPak(systemEntity);

        Long systemId = systemEntity.getId();
        FcAuthPak auth = getAuthBySysId(systemId);

        Map<String, FcApiPak> apis = getApisBySysId(systemId);

        return FcSourcePak.builder()
                .system(system)
                .auth(auth)
                .apiMap(apis)
                .build();
    }

    @Override
    public void tryUpdateCredential(String systemCode, ICredential credential) {
    }

    @Override
    public int getWeight() {
        return 1;
    }

    private FcSystem getSystemByCode(String systemCode) {
        LambdaQueryWrapper<FcSystem> systemQw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, systemCode);
        return systemMapper.selectOne(systemQw);
    }

    private FcAuthPak getAuthBySysId(Long sysId) {
        LambdaQueryWrapper<FcAuth> authQw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, sysId);
        FcAuth auth = authMapper.selectOne(authQw);
        if (Objects.isNull(auth)) {
            return null;
        }

        return pakMapping.toAuthPak(auth);
    }

    private Map<String, FcApiPak> getApisBySysId(Long sysId) {
        LambdaQueryWrapper<FcApi> apiQw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, sysId);
        List<FcApi> apiList = apiMapper.selectList(apiQw);
        if (CollectionUtil.isEmpty(apiList)) {
            return Collections.emptyMap();
        }

        return fillWithApiParams(apiList);
    }

    private Map<String, FcApiPak> fillWithApiParams(List<FcApi> apiList) {
        List<Long> apiIds = apiList.stream().map(FcApi::getId).collect(Collectors.toList());
        LambdaQueryWrapper<FcApiParam> inApiIdQw = Wrappers.<FcApiParam>lambdaQuery().in(FcApiParam::getApiId, apiIds);
        List<FcApiParam> apiParamList = apiParamMapper.selectList(inApiIdQw);
        Map<Long, List<FcApiParam>> apiParamMap = apiParamList.stream().collect(Collectors.groupingBy(FcApiParam::getApiId));

        return apiList.stream()
                .map(api -> {
                    FcApiPak apiPak = pakMapping.toApiPak(api);
                    List<FcApiParam> apiParams = apiParamMap.get(api.getId());
                    FcApiParamPak params = FcApiParamPak.from(apiParams);
                    apiPak.setParams(params);
                    return apiPak;
                })
                .collect(Collectors.toMap(FcApiPak::getName, Function.identity()));
    }

}
