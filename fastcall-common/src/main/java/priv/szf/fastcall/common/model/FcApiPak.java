package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import priv.szf.fastcall.common.FcRequestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcApiPak implements IFcPak {

    private static final long serialVersionUID = -3294588741274820809L;

    private final String name;

    private final String path;

    private final FcRequestMethod method;

    private final String particularHost;

    private final FcClientSettingPak clientSetting;

    private final FcApiParamPak paramPak;

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Arrays.asList(
                this::getName,
                this::getPath,
                this::getMethod
        );
    }
}
