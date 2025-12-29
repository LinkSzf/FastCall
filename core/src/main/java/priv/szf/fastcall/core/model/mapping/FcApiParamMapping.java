package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.core.model.dto.FcApiParamDTO;
import priv.szf.fastcall.core.model.entity.FcApiParam;
import priv.szf.fastcall.core.model.vo.FcApiParamVO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcApiParamMapping {

    List<FcApiParamVO> toVoList(List<FcApiParam> list);


    List<FcApiParam> toEntityList(List<FcApiParamDTO> dtoList);
}
