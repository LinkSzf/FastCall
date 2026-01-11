package priv.szf.fastcall.data.pak;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.common.FcFuncScope;
import priv.szf.fastcall.common.FcParamPos;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcApiParamPak;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcHeaderAssignPak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.data.FcDataConsts;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcApiParam;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.entity.FcSystem;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring"
)
public interface FcPakMapping {

    @Mapping(target = "clientSetting.connectTimeout", source = "connectTimeout")
    @Mapping(target = "clientSetting.readTimeout", source = "readTimeout")
    @Mapping(target = "clientSetting.writeTimeout", source = "writeTimeout")
    FcSystemPak toSystemPak(FcSystem system);

    @Mapping(target = "content", source = "auth")
    FcAuthPak toAuthPak(FcAuth auth);

    @Mapping(target = "clientSetting.connectTimeout", source = "connectTimeout")
    @Mapping(target = "clientSetting.readTimeout", source = "readTimeout")
    @Mapping(target = "clientSetting.writeTimeout", source = "writeTimeout")
    FcApiPak toApiPak(FcApi api);

    List<FcHeaderAssignPak> toHeaderAssignPak(List<FcHeaderAssign> headerAssign);

    default BaseAuthContent toBean(FcAuth entity){
        return JSONUtil.toBean(entity.getContent(), entity.getType().getClazz());
    }

    default Set<FcFuncScope> toEnumSet(String scope) {
        if (StrUtil.isBlank(scope)) {
            return Collections.emptySet();
        }
        return Arrays.stream(scope.split(FcDataConsts.SYSTEM_FUNC_SCOPE_DELIMITER))
                .map(String::trim)
                .map(FcFuncScope::valueOf)
                .collect(Collectors.toSet());
    }

    default FcApiParamPak toApiParamPak(List<FcApiParam> apiParams) {
        if (CollectionUtil.isEmpty(apiParams)) {
            return FcApiParamPak.empty();
        }

        FcApiParamPak paramPak = FcApiParamPak.builder()
                .headers(new HashMap<>())
                .params(new HashMap<>())
                .build();

        apiParams.stream()
                .filter(Objects::nonNull)
                .forEach(param -> {
                    FcParamPos position = param.getPosition();
                    String name = param.getName();
                    String defaultValue = param.getDefaultValue();
                    if (position == FcParamPos.HEADER) {
                        paramPak.addHeader(name, defaultValue);
                    } else if (position == FcParamPos.QUERY) {
                        paramPak.addParam(name, defaultValue);
                    } else if (position == FcParamPos.BODY) {
                        Object body = defaultValue;
                        if (Boolean.TRUE.equals(param.getJsonObj())) {
                            body = JSONUtil.parse(defaultValue);
                        }
                        paramPak.setBody(body);
                    }
                });

        return paramPak;
    }
}
