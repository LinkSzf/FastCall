package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.FcRequestMethod;

import java.io.Serializable;

@Data
public class FcApiPak implements Serializable {

    private static final long serialVersionUID = -3294588741274820809L;

    private String name;

    private String path;

    private FcRequestMethod method;

    private String particularHost;

    private FcClientSettingPak clientSetting;

    private FcApiParamPak params;

}
