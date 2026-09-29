package io.github.linkszf.fastcall.data.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import io.github.linkszf.fastcall.common.model.FcApiPak;
import io.github.linkszf.fastcall.common.model.FcApiParamPak;
import io.github.linkszf.fastcall.common.model.FcAuthPak;
import io.github.linkszf.fastcall.common.model.FcRateLimitPak;
import io.github.linkszf.fastcall.common.model.FcRetryPak;
import io.github.linkszf.fastcall.common.model.FcSystemPak;
import io.github.linkszf.fastcall.common.source.IFcPakProvider;
import io.github.linkszf.fastcall.data.entity.FcApi;
import io.github.linkszf.fastcall.data.entity.FcApiParam;
import io.github.linkszf.fastcall.data.entity.FcAuth;
import io.github.linkszf.fastcall.data.entity.FcRateLimit;
import io.github.linkszf.fastcall.data.entity.FcRetry;
import io.github.linkszf.fastcall.data.entity.FcSystem;
import io.github.linkszf.fastcall.data.mapper.FcApiDao;
import io.github.linkszf.fastcall.data.mapper.FcApiParamDao;
import io.github.linkszf.fastcall.data.mapper.FcAuthDao;
import io.github.linkszf.fastcall.data.mapper.FcRateLimitDao;
import io.github.linkszf.fastcall.data.mapper.FcRetryDao;
import io.github.linkszf.fastcall.data.mapper.FcSystemDao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FcDefaultPakProvider implements IFcPakProvider {

    private final Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private final FcSystemDao systemDao;

    private final FcAuthDao authDao;

    private final FcApiDao apiDao;

    private final FcApiParamDao apiParamDao;

    private final FcRateLimitDao rateLimitDao;

    private final FcRetryDao retryDao;

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
    public FcRetryPak getRetryBySysId(Long sysId) {
        FcRetry retry = retryDao.getBySystemId(sysId);
        return pakMapping.toRetryPak(retry);
    }

    @Override
    public List<FcRateLimitPak> getAllRateLimits() {
        List<FcRateLimit> list = rateLimitDao.listAllEnable();
        return pakMapping.toRateLimitPak(list);
    }

    @Override
    public void saveRateLimits(Long sysId, List<FcRateLimitPak> rateLimitPaks) {
        ReentrantLock lock = this.lockMap.computeIfAbsent(sysId, key -> new ReentrantLock(true));
        lock.lock();
        try {
            List<FcRateLimit> list = pakMapping.toRateLimit(rateLimitPaks);
            rateLimitDao.saveBatch(list);
        } finally {
            lock.unlock();
        }
    }

    private Map<String, FcApiPak> fillWithApiParams(List<FcApi> apiList) {
        List<Long> apiIds = apiList.stream().map(FcApi::getId).distinct().collect(Collectors.toList());
        List<FcApiParam> apiParamList = apiParamDao.listByApiIds(apiIds);
        Map<Long, List<FcApiParam>> apiParamMap = apiParamList.stream().collect(Collectors.groupingBy(FcApiParam::getApiId));

        return apiList.stream()
                .map(api -> {
                    List<FcApiParam> apiParams = apiParamMap.get(api.getId());
                    FcApiParamPak params = pakMapping.toApiParamPak(apiParams);
                    return pakMapping.toApiPak(api, params);
                })
                .collect(Collectors.toMap(FcApiPak::getName, Function.identity()));
    }
}
