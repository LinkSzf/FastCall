package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import priv.szf.fastcall.common.FcAuthType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcSystemPak implements IFcPak {

    private static final long serialVersionUID = -3705370467972208682L;

    private final Long id;

    private final String name;

    private final String code;

    private final boolean enable;

    private final String host;

    private final FcAuthType authType;

    private final FcClientSettingPak clientSetting;

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Arrays.asList(
                this::getId,
                this::getName,
                this::getCode,
                this::getHost,
                this::getAuthType
        );
    }

}
