package io.github.linkszf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import io.github.linkszf.fastcall.api.model.dto.FcAuthDTO;
import io.github.linkszf.fastcall.api.model.vo.FcAuthVO;
import io.github.linkszf.fastcall.data.entity.FcAuth;

@Mapper(
        componentModel = "spring",
        uses = {FcAuthContentConverter.class}
)
public interface FcAuthMapping {

    @Mapping(target = "content", source = "entity")
    FcAuthVO toVo(FcAuth entity);

    FcAuth toEntity(FcAuthDTO dto);

}
