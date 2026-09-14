package priv.szf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import priv.szf.fastcall.api.model.dto.FcRetryDTO;
import priv.szf.fastcall.api.model.vo.FcRetryVO;
import priv.szf.fastcall.data.entity.FcRetry;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface FcRetryMapping {

    FcRetryVO toVo(FcRetry entity);

    List<FcRetryVO> toVoList(List<FcRetry> list);

    @Mapping(target = "sysId", ignore = true)
    FcRetry toEntity(FcRetryDTO dto);

}
