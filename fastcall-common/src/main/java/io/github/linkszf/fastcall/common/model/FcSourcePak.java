package io.github.linkszf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Builder
@Getter
public class FcSourcePak implements IFcPak {

    private static final long serialVersionUID = 1169371931895544090L;

    private final FcSystemPak system;

    private final FcAuthPak auth;

    private final FcRetryPak retry;

    @Setter
    private volatile ICredential credential;

    private final Map<String, FcApiPak> apiMap;

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.singletonList(
                this::getSystem
        );
    }
}
