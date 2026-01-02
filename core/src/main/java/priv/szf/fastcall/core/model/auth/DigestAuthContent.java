package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DigestAuthContent extends BaseDynAuthContent {

    private String username;

    private String password;

}
