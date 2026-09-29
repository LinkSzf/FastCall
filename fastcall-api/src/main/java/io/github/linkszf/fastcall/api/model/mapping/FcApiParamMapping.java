package io.github.linkszf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import io.github.linkszf.fastcall.api.model.dto.FcApiParamDTO;
import io.github.linkszf.fastcall.api.model.vo.FcApiParamVO;
import io.github.linkszf.fastcall.data.entity.FcApiParam;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcApiParamMapping {

    List<FcApiParamVO> toVoList(List<FcApiParam> list);


    List<FcApiParam> toEntityList(List<FcApiParamDTO> dtoList);
}
