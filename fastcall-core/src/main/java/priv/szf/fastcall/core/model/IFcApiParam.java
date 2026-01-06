package priv.szf.fastcall.core.model;

import priv.szf.fastcall.common.FcParamPos;

public interface IFcApiParam {

    String getName();

    FcParamPos getPosition();

    Boolean getJsonObj();

    String getDefaultValue();
}
