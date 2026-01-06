package priv.szf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.api.model.dto.FcAuthDTO;
import priv.szf.fastcall.api.model.vo.FcAuthVO;
import priv.szf.fastcall.data.entity.FcAuth;

@Mapper(
        componentModel = "spring",
        uses = {FcAuthContentConverter.class}
)
public interface FcAuthMapping {

    @Mapping(target = "content", source = "entity")
    FcAuthVO toVo(FcAuth entity);

    FcAuth toEntity(FcAuthDTO dto);

}
