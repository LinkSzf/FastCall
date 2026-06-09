package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.model.content.BaseAuthContent;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
public class FcAuthPak implements IEssentialCheck<FcAuthPak>, Serializable {

    private static final long serialVersionUID = 8346425238219909450L;

    private BaseAuthContent content;

    private Integer expiration;

    private String path;

    private String particularHost;

    private Integer unauthorizedCode;


    @Override
    public List<Function<FcAuthPak, ?>> requireNonNull() {
        return Collections.singletonList(
                FcAuthPak::getContent
        );
    }
}
