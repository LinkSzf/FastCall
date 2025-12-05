package priv.szf.fastcall.core.model;

import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;

@Data
public class FcAuthPak {


    private AuthType type;

    private String content;

    private Integer expiration;

    private String particularHost;
}
