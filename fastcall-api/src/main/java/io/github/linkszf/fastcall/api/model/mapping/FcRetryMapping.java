package io.github.linkszf.fastcall.api.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import io.github.linkszf.fastcall.api.model.dto.FcRetryDTO;
import io.github.linkszf.fastcall.api.model.vo.FcRetryVO;
import io.github.linkszf.fastcall.data.entity.FcRetry;

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
