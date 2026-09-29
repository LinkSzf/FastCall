package io.github.linkszf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcClientSettingPak implements IFcPak {

    private static final long serialVersionUID = -7562993860194630639L;

    private final Integer connectTimeout;

    private final Integer readTimeout;

    private final Integer writeTimeout;

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.emptyList();
    }
}
