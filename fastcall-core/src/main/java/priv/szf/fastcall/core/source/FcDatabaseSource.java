package priv.szf.fastcall.core.source;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcApiParamPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.credential.ICredential;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcApiParam;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.core.model.mapping.FcPakMapping;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

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

    private final FcSystemDao systemDao;

    private final FcAuthDao authDao;

    private final FcApiDao apiDao;

    private final FcApiParamDao apiParamDao;

    private final FcPakMapping pakMapping;


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
        return systemDao.getByCode(systemCode);
    }

    private FcAuthPak getAuthBySysId(Long sysId) {
        FcAuth auth = authDao.getBySystemId(sysId);
        return pakMapping.toAuthPak(auth);
    }

    private Map<String, FcApiPak> getApisBySysId(Long sysId) {
        List<FcApi> apiList = apiDao.listBySystemId(sysId);
        if (CollectionUtil.isEmpty(apiList)) {
            return Collections.emptyMap();
        }

        return fillWithApiParams(apiList);
    }

    private Map<String, FcApiPak> fillWithApiParams(List<FcApi> apiList) {
        List<Long> apiIds = apiList.stream().map(FcApi::getId).distinct().collect(Collectors.toList());
        List<FcApiParam> apiParamList = apiParamDao.listByApiIds(apiIds);
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
