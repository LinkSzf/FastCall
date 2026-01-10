package priv.szf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import priv.szf.fastcall.api.model.dto.FcSystemDTO;
import priv.szf.fastcall.api.model.vo.FcSystemVO;
import priv.szf.fastcall.data.entity.FcSystem;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {FcAuthMapping.class,
                FcSystemFuncScopeConverter.class}
)
public interface FcSystemMapping {
    FcSystemVO toVo(FcSystem entity);

    List<FcSystemVO> toVoList(List<FcSystem> list);


    FcSystem toEntity(FcSystemDTO dto);
}
