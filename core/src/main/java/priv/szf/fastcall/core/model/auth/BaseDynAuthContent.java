package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseDynAuthContent extends BaseAuthContent {

    private FcAuthProp prop;

    private String tokenField;

    private String expiredInField;

    private String issuanceField;

}
