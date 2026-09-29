package io.github.linkszf.fastcall.data.provider;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import io.github.linkszf.fastcall.common.FcParamPos;
import io.github.linkszf.fastcall.common.model.FcApiPak;
import io.github.linkszf.fastcall.common.model.FcApiParamPak;
import io.github.linkszf.fastcall.common.model.FcAuthPak;
import io.github.linkszf.fastcall.common.model.FcRateLimitPak;
import io.github.linkszf.fastcall.common.model.FcRetryPak;
import io.github.linkszf.fastcall.common.model.FcSystemPak;
import io.github.linkszf.fastcall.common.model.content.BaseAuthContent;
import io.github.linkszf.fastcall.data.entity.FcApi;
import io.github.linkszf.fastcall.data.entity.FcApiParam;
import io.github.linkszf.fastcall.data.entity.FcAuth;
import io.github.linkszf.fastcall.data.entity.FcRateLimit;
import io.github.linkszf.fastcall.data.entity.FcRetry;
import io.github.linkszf.fastcall.data.entity.FcSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface FcPakMapping {

    @Mapping(target = "clientSetting.connectTimeout", source = "connectTimeout")
    @Mapping(target = "clientSetting.readTimeout", source = "readTimeout")
    @Mapping(target = "clientSetting.writeTimeout", source = "writeTimeout")
    FcSystemPak toSystemPak(FcSystem system);

    @Mapping(target = "content", source = "auth")
    FcAuthPak toAuthPak(FcAuth auth);

    @Mapping(target = "clientSetting.connectTimeout", source = "api.connectTimeout")
    @Mapping(target = "clientSetting.readTimeout", source = "api.readTimeout")
    @Mapping(target = "clientSetting.writeTimeout", source = "api.writeTimeout")
    FcApiPak toApiPak(FcApi api, FcApiParamPak paramPak);

    @Mapping(target = "systemId", source = "sysId")
    FcRateLimitPak toRateLimitPak(FcRateLimit rateLimit);

    FcRetryPak toRetryPak(FcRetry retry);

    List<FcRateLimitPak> toRateLimitPak(List<FcRateLimit> rateLimits);

    @Mapping(target = "sysId", source = "systemId")
    @Mapping(target = "enable", constant = "true")
    FcRateLimit toRateLimit(FcRateLimitPak rateLimitPak);

    List<FcRateLimit> toRateLimit(List<FcRateLimitPak> rateLimitPaks);


    default BaseAuthContent toBean(FcAuth entity){
        return JSONUtil.toBean(entity.getContent(), entity.getType().getClazz());
    }

    default FcApiParamPak toApiParamPak(List<FcApiParam> apiParams) {
        if (CollectionUtil.isEmpty(apiParams)) {
            return null;
        }

        Map<String, String> headers = new HashMap<>(apiParams.size());
        Map<String, String> params = new HashMap<>(apiParams.size());
        AtomicReference<Object> bodyRef = new AtomicReference<>();
        apiParams.stream()
                .filter(Objects::nonNull)
                .forEach(param -> {
                    FcParamPos position = param.getPosition();
                    String name = param.getName();
                    String defaultValue = param.getDefaultValue();
                    if (position == FcParamPos.HEADER) {
                        headers.put(name, defaultValue);
                    } else if (position == FcParamPos.QUERY) {
                        params.put(name, defaultValue);
                    } else if (position == FcParamPos.BODY) {
                        Object body = (Boolean.TRUE.equals(param.getJsonObj())) ?
                                JSONUtil.parse(defaultValue) : defaultValue;
                        bodyRef.set(body);
                    }
                });

        return FcApiParamPak.builder()
                .headers(headers)
                .params(params)
                .body(bodyRef.get())
                .build();
    }

}
