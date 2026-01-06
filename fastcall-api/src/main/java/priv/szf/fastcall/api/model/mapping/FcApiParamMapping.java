package priv.szf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.api.model.dto.FcApiParamDTO;
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.data.entity.FcApiParam;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcApiParamMapping {

    List<FcApiParamVO> toVoList(List<FcApiParam> list);


    List<FcApiParam> toEntityList(List<FcApiParamDTO> dtoList);
}
