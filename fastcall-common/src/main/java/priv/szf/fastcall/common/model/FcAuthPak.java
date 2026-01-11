package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.content.BaseAuthContent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
public class FcAuthPak implements IEssentialCheck<FcAuthPak>, Serializable {

    private static final long serialVersionUID = 8346425238219909450L;

    private FcAuthType type;

    private BaseAuthContent content;

    private Integer expiration;

    private String path;

    private String particularHost;


    @Override
    public List<Function<FcAuthPak, ?>> requireNonNull() {
        return Arrays.asList(
                FcAuthPak::getType,
                FcAuthPak::getContent
        );
    }
}
