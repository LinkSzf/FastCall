package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.core.model.dto.FcAuthDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.vo.FcAuthVO;

@Mapper(
        componentModel = "spring",
        uses = {FcAuthContentConverter.class}
)
public interface FcAuthMapping {

    @Mapping(target = "content", source = "entity")
    FcAuthVO toVo(FcAuth entity);

    @Mapping(target = "content", source = "dto")
    FcAuth toEntity(FcAuthDTO dto);

}
