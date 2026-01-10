package priv.szf.fastcall.core.model;

import lombok.Data;
import priv.szf.fastcall.common.FcFuncScope;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Data
public class FcSystemPak implements IEssentialCheck<FcSystemPak>, Serializable {

    private static final long serialVersionUID = -3705370467972208682L;

    private String name;

    private String code;

    private boolean enable;

    private Set<FcFuncScope> scope;

    private String host;

    private FcClientSettingPak clientSetting;

    @Override
    public List<Function<FcSystemPak, ?>> requireNonNull() {
        return Arrays.asList(
                FcSystemPak::getName,
                FcSystemPak::getCode,
                FcSystemPak::getHost
        );
    }

}
