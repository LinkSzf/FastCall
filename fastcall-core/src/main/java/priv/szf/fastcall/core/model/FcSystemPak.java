package priv.szf.fastcall.core.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
public class FcSystemPak implements IEssentialCheck<FcSystemPak> {

    private String name;

    private String code;

    private boolean enable;

    private String host;

    private FcClientSettingPak clientSetting;

    @Override
    public List<Function<FcSystemPak, ?>> checkThese() {
        return Arrays.asList(
                FcSystemPak::getName,
                FcSystemPak::getCode,
                FcSystemPak::getHost
        );
    }

}
