package priv.szf.fastcall.core.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.model.auth.FcAuthProp;


@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDynAuthContentDTO extends BaseAuthContentDTO {

    private FcAuthProp prop;

    private String tokenField;

    private String expiredInField;

    private String issuanceField;

}
