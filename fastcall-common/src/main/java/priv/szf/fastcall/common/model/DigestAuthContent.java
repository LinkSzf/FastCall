package priv.szf.fastcall.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DigestAuthContent extends BaseDynAuthContent {

    private String username;

    private String password;

}
