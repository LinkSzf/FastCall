package priv.szf.fastcall.core.model;

import lombok.Data;

@Data
public class FcApiPak {

    private String name;

    private String path;

    private String method;

    private String particularHost;

    private FcClientSettingPak clientSetting;

}
