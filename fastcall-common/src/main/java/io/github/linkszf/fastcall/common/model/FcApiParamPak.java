package io.github.linkszf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Builder
@Getter
public class FcApiParamPak implements IFcPak {

    private static final long serialVersionUID = -2201090540519752028L;

    private final Map<String, String> headers;

    private final Map<String, String> params;

    private final Object body;


    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.emptyList();
    }
}
