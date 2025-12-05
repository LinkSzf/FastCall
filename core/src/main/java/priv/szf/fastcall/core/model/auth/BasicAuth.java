package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasicAuth extends BaseAuthContent {

    private String username;

    private String password;

}
