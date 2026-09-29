package io.github.linkszf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import io.github.linkszf.fastcall.api.model.dto.FcApiDTO;
import io.github.linkszf.fastcall.api.model.vo.FcApiVO;
import io.github.linkszf.fastcall.data.entity.FcApi;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcApiMapping {

    List<FcApiVO> toVoList(List<FcApi> list);


    List<FcApi> toEntityList(List<FcApiDTO> dtoList);
}
