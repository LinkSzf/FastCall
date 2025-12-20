package priv.szf.fastcall.core.call.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.auth.FcAuthProp;


@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDynAuthContent extends BaseAuthContent {

    private FcAuthProp prop;

    private String tokenField;

    private String expiredInField;

    private String issuanceField;

}
