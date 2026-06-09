package priv.szf.fastcall.core.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.model.IEssentialCheck;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Builder
@Getter
public class FcRequestContext implements IEssentialCheck<FcRequestContext> {

    private final String system;

    private final FcAuthType authType;

    private final FcCallType callType;

    private final InterceptorContext interceptorContext = new InterceptorContext();

    @Override
    public List<Function<FcRequestContext, ?>> requireNonNull() {
        return Arrays.asList(
                FcRequestContext::getSystem,
                FcRequestContext::getAuthType,
                FcRequestContext::getCallType
        );
    }

    @Data
    public class InterceptorContext {

        private boolean skipAuth = false;

        private boolean needRetry = false;

        private byte[] requestBodySnapshot;
    }
}
