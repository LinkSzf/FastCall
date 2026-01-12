package priv.szf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.api.model.dto.FcHeaderAssignDTO;
import priv.szf.fastcall.api.model.vo.FcHeaderAssignVO;
import priv.szf.fastcall.data.entity.FcHeaderAssign;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FcHeaderAssignMapping {

    List<FcHeaderAssignVO> toVoList(List<FcHeaderAssign> list);

    FcHeaderAssignVO toVo(FcHeaderAssign entity);

    FcHeaderAssign toEntity(FcHeaderAssignDTO dto);


    List<FcHeaderAssign> toEntityList(List<FcHeaderAssignDTO> dtoList);
}
