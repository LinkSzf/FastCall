package priv.szf.fastcall.core.model.mapping;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.common.FcFuncScope;
import priv.szf.fastcall.common.model.BaseAuthContent;
import priv.szf.fastcall.core.model.FcHeaderAssignPak;
import priv.szf.fastcall.data.FcDataConsts;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcClientSettingPak;
import priv.szf.fastcall.core.model.FcSystemPak;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    FcClientSettingPak toClientSettingPak(FastCallProperties.Client clientSetting);

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

}
