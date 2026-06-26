package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcFuncScope;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Builder
@Getter
public class FcSystemPak implements IEssentialCheck<FcSystemPak>, Serializable {

    private static final long serialVersionUID = -3705370467972208682L;

    private final Long id;

    private final String name;

    private final String code;

    private final boolean enable;

    private final Set<FcFuncScope> scope;

    private final String host;

    private final FcAuthType authType;

    private final FcClientSettingPak clientSetting;

    @Override
    public List<Function<FcSystemPak, ?>> requireNonNull() {
        return Arrays.asList(
                FcSystemPak::getId,
                FcSystemPak::getName,
                FcSystemPak::getCode,
                FcSystemPak::getHost,
                FcSystemPak::getAuthType
        );
    }

}
