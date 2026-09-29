package io.github.linkszf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import io.github.linkszf.fastcall.api.model.dto.FcSystemDTO;
import io.github.linkszf.fastcall.api.model.vo.FcSystemVO;
import io.github.linkszf.fastcall.data.entity.FcSystem;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {FcAuthMapping.class, FcRetryMapping.class}
)
public interface FcSystemMapping {
    FcSystemVO toVo(FcSystem entity);

    List<FcSystemVO> toVoList(List<FcSystem> list);


    FcSystem toEntity(FcSystemDTO dto);
}
