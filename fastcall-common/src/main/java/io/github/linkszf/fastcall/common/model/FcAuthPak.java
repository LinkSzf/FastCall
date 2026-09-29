package io.github.linkszf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import io.github.linkszf.fastcall.common.model.content.BaseAuthContent;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcAuthPak implements IFcPak {

    private static final long serialVersionUID = 8346425238219909450L;

    private final BaseAuthContent content;

    private final String path;

    private final String particularHost;

    private final Integer unauthorizedCode;


    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.singletonList(
                this::getContent
        );
    }
}
