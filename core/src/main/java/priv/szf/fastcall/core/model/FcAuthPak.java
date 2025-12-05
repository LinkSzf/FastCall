package priv.szf.fastcall.core.model;

import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

@Data
public class FcAuthPak {

    private AuthType type;

    private BaseAuthContent content;

    private Integer expiration;

    private String particularHost;
}
