package priv.szf.fastcall.core.call.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDynAuthContent extends BaseAuthContent {

    private Map<String, String> params;

    private String tokenField;

    private String expiredInField;

    private String issuanceField;

}
