package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import priv.szf.fastcall.common.FcRequestMethod;

import java.io.Serializable;

@Builder
@Getter
public class FcApiPak implements Serializable {

    private static final long serialVersionUID = -3294588741274820809L;

    private final String name;

    private final String path;

    private final FcRequestMethod method;

    private final String particularHost;

    private final FcClientSettingPak clientSetting;

    private final FcApiParamPak paramPak;

}
