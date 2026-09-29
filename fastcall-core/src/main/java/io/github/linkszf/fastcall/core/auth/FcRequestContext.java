package io.github.linkszf.fastcall.core.auth;

import lombok.Builder;
import lombok.Getter;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.FcCallType;
import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.common.model.IFcNonNullModel;
import io.github.linkszf.fastcall.core.FastCallClient;

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

    @Getter
    public static class InterceptorContext {

        private boolean skipAuth = false;

        private boolean needRetry = false;

        public void reverseRetry() {
            this.needRetry = !this.needRetry;
        }

        public void markSkipAuth() {
            this.skipAuth = true;
        }
    }
}
