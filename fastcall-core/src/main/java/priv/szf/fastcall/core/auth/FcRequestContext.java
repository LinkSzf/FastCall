package priv.szf.fastcall.core.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.IFcNonNullModel;
import priv.szf.fastcall.core.FastCallClient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcRequestContext implements IFcNonNullModel {

    private final String system;

    private final FastCallClient client;

    private final FcSourcePak source;

    private final FcAuthType authType;

    private final FcCallType callType;

    private final InterceptorContext interceptorContext = new InterceptorContext();

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Arrays.asList(
                this::getSystem,
                this::getAuthType,
                this::getCallType
        );
    }

    @Data
    public static class InterceptorContext {

        private boolean skipAuth = false;

        private boolean recall = false;

        private byte[] requestBodySnapshot;
    }
}
