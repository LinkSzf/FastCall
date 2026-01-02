package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.AuthContentConverter;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcClientSettingPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;

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

}
