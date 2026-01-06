package priv.szf.fastcall.core.model;

import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.BaseAuthContent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
public class FcAuthPak implements IEssentialCheck<FcAuthPak>{

    private FcAuthType type;

    private BaseAuthContent content;

    private Integer expiration;

    private String path;

    private String particularHost;


    @Override
    public List<Function<FcAuthPak, ?>> checkThese() {
        return Arrays.asList(
                FcAuthPak::getType,
                FcAuthPak::getContent,
                FcAuthPak::getPath
        );
    }
}
