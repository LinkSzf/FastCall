package io.github.linkszf.fastcall.api.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.linkszf.fastcall.common.model.content.FcAuthProp;


@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDynAuthContentDTO extends BaseAuthContentDTO {

    private FcAuthProp prop;

}
