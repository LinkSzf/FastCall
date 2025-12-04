package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.core.model.dto.FcApiDTO;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.vo.FcApiVO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcApiMapping {

    List<FcApiVO> toVoList(List<FcApi> list);


    List<FcApi> toEntityList(List<FcApiDTO> dtoList);
}
