package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import priv.szf.fastcall.common.model.content.BaseAuthContent;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Builder
@Getter
public class FcAuthPak implements IEssentialCheck<FcAuthPak>, Serializable {

    private static final long serialVersionUID = 8346425238219909450L;

    private final BaseAuthContent content;

    private final Integer expiration;

    private final String path;

    private final String particularHost;

    private final Integer unauthorizedCode;


    @Override
    public List<Function<FcAuthPak, ?>> requireNonNull() {
        return Collections.singletonList(
                FcAuthPak::getContent
        );
    }
}
