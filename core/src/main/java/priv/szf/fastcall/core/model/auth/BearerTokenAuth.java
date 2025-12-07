package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.call.auth.IRefreshableAuth;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class BearerTokenAuth extends BaseAuthContent implements IRefreshableAuth {

    private String fixedToken;

    private Map<String, String> params;

    private String tokenField;

}
