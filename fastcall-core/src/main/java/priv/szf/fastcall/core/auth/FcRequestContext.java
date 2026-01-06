package priv.szf.fastcall.core.auth;

import lombok.Builder;
import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.core.model.IEssentialCheck;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Builder
@Data
public class FcRequestContext implements IEssentialCheck<FcRequestContext> {

    private final String system;

    private final FcAuthType authType;

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
