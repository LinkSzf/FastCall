package priv.szf.fastcall.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FastCallConsts {

    public static final String NAME = "FastCall";

    public static final int AUTO_CONFIGURATION_ORDER = 1000;

    public static final int AUTO_CONFIGURATION_DATA_ORDER = AUTO_CONFIGURATION_ORDER + 1;

    public static final int AUTO_CONFIGURATION_CORE_ORDER = AUTO_CONFIGURATION_ORDER + 2;


}
