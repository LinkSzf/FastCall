package priv.szf.fastcall.core.call.auth;

import lombok.Builder;
import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcCallType;

@Builder
@Data
public class FcRequestContext {

    private final String system;

    private final AuthType authType;

    private final FcCallType callType;
}
