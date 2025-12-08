package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.core.call.auth.BaseDynAuthContent;

@EqualsAndHashCode(callSuper = true)
@Data
public class BearerTokenAuth extends BaseDynAuthContent {

    private String fixedToken;

}
