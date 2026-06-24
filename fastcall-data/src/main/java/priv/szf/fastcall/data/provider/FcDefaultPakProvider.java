package priv.szf.fastcall.data.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcApiParamPak;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcHeaderAssignPak;
import priv.szf.fastcall.common.model.FcRateLimitPak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcApiParam;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.entity.FcRateLimit;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcApiParamDao;
import priv.szf.fastcall.data.mapper.FcAuthDao;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;
import priv.szf.fastcall.data.mapper.FcRateLimitDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FcDefaultPakProvider implements IFcPakProvider {

    private final FcSystemDao systemDao;

    private final FcAuthDao authDao;

    private final FcApiDao apiDao;

    private final FcApiParamDao apiParamDao;

    private final FcHeaderAssignDao headerAssignDao;

    private final FcRateLimitDao rateLimitDao;

    private final FcPakMapping pakMapping;


    @Override
    public FcSystemPak getSystemByCode(String systemCode) {
        FcSystem system = systemDao.getByCode(systemCode);
        return pakMapping.toSystemPak(system);
    }

    @Override
    public FcAuthPak getAuthBySysId(Long sysId) {
        FcAuth auth = authDao.getBySystemId(sysId);
        return pakMapping.toAuthPak(auth);
    }

    @Override
    public Map<String, FcApiPak> getApisBySysId(Long sysId) {
        List<FcApi> apiList = apiDao.listBySystemId(sysId);
        if (CollectionUtil.isEmpty(apiList)) {
            return Collections.emptyMap();
        }

        return fillWithApiParams(apiList);
    }

    @Override
    public List<FcHeaderAssignPak> getHeaderAssignsBySysId(Long systemId) {
        List<FcHeaderAssign> headerAssignList = headerAssignDao.listBySystemId(systemId);
        return pakMapping.toHeaderAssignPak(headerAssignList);
    }

    @Override
    public List<FcRateLimitPak> getAllRateLimits() {
        List<FcRateLimit> list = rateLimitDao.listAllEnable();
        return pakMapping.toRateLimitPak(list);
    }

    @Override
    public void saveRateLimits(List<FcRateLimitPak> rateLimitPaks) {
        List<FcRateLimit> list = pakMapping.toRateLimit(rateLimitPaks);
        rateLimitDao.saveBatch(list);
    }

    private Map<String, FcApiPak> fillWithApiParams(List<FcApi> apiList) {
        List<Long> apiIds = apiList.stream().map(FcApi::getId).distinct().collect(Collectors.toList());
        List<FcApiParam> apiParamList = apiParamDao.listByApiIds(apiIds);
        Map<Long, List<FcApiParam>> apiParamMap = apiParamList.stream().collect(Collectors.groupingBy(FcApiParam::getApiId));

        return apiList.stream()
                .map(api -> {
                    FcApiPak apiPak = pakMapping.toApiPak(api);
                    List<FcApiParam> apiParams = apiParamMap.get(api.getId());
                    FcApiParamPak params = pakMapping.toApiParamPak(apiParams);
                    apiPak.setParams(params);
                    return apiPak;
                })
                .collect(Collectors.toMap(FcApiPak::getName, Function.identity()));
    }
}
