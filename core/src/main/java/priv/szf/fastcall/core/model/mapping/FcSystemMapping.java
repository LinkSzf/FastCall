package priv.szf.fastcall.core.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.core.model.dto.FcSystemDTO;
import priv.szf.fastcall.core.model.entity.FcSystem;
import priv.szf.fastcall.core.model.vo.FcSystemVO;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {FcAuthMapping.class}
)
public interface FcSystemMapping {
    FcSystemVO toVo(FcSystem entity);

    List<FcSystemVO> toVoList(List<FcSystem> list);


    FcSystem toEntity(FcSystemDTO dto);
}
