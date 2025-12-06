package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;

@Data
public abstract class BaseAuthContent {

    private AuthType type;

}
