package priv.szf.fastcall.core.call.source;

import priv.szf.fastcall.core.model.FcTokenPak;

public interface IFcSource {

//    FcSourcePak getSourcePak(String systemCode, String apiName);

    FcSourcePak getSourcePak(String systemCode);

    FcTokenPak getAccessToken(String systemCode);

}
