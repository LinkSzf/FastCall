package priv.szf.fastcall.core.call.auth;

import lombok.Builder;
import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcCallType;
import priv.szf.fastcall.core.model.IEssentialCheck;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Builder
@Data
public class FcRequestContext implements IEssentialCheck<FcRequestContext> {

    private final String system;

    private final AuthType authType;

    private final FcCallType callType;

    @Override
    public List<Function<FcRequestContext, ?>> checkThese() {
        return Arrays.asList(
                FcRequestContext::getSystem,
                FcRequestContext::getAuthType,
                FcRequestContext::getCallType
        );
    }
}
