package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.*;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.entity.FcToken;

@Mapper(
        componentModel = "spring",
        uses = {AuthContentConverter.class}
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

    FcClientSettingPak toClientSettingPak(FastCallProperties.Client clientSetting);

    FcTokenPak toTokenPak(FcToken token);

    FcToken toToken(FcTokenPak token);
}
