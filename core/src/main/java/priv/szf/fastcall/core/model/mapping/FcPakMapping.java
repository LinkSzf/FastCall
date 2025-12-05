package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcPakMapping {

    @Mapping(target = "clientSetting.connectTimeout", source = "connectTimeout")
    @Mapping(target = "clientSetting.readTimeout", source = "readTimeout")
    @Mapping(target = "clientSetting.writeTimeout", source = "writeTimeout")
    FcSystemPak toSystemPak(FcSystem system);

    FcAuthPak toAuthPak(FcAuth auth);

    @Mapping(target = "clientSetting.connectTimeout", source = "connectTimeout")
    @Mapping(target = "clientSetting.readTimeout", source = "readTimeout")
    @Mapping(target = "clientSetting.writeTimeout", source = "writeTimeout")
    FcApiPak toApiPak(FcApi api);

}
