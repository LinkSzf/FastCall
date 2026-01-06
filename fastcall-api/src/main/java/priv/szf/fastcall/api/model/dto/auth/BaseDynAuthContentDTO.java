package priv.szf.fastcall.api.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.common.model.FcAuthProp;


@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDynAuthContentDTO extends BaseAuthContentDTO {

    private FcAuthProp prop;

    private String tokenField;

    private String expiredInField;

    private String issuanceField;

}
