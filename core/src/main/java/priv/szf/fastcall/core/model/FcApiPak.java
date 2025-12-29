package priv.szf.fastcall.core.model;

import lombok.Data;
import priv.szf.fastcall.core.common.FcRequestMethod;

@Data
public class FcApiPak {

    private String name;

    private String path;

    private FcRequestMethod method;

    private String particularHost;

    private FcClientSettingPak clientSetting;

    private FcApiParamPak params;

}
