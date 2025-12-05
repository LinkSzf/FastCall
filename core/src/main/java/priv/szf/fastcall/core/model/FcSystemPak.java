package priv.szf.fastcall.core.model;

import lombok.Data;

@Data
public class FcSystemPak {

    private String name;

    private String code;

    private boolean enable;

    private String host;

    private FcClientSettingPak clientSetting;

}
