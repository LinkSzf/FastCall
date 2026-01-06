package priv.szf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.api.model.dto.FcApiDTO;
import priv.szf.fastcall.api.model.vo.FcApiVO;
import priv.szf.fastcall.data.entity.FcApi;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcApiMapping {

    List<FcApiVO> toVoList(List<FcApi> list);


    List<FcApi> toEntityList(List<FcApiDTO> dtoList);
}
