package priv.szf.fastcall.core.call.source;

public interface IFcSource {

//    FcSourcePak getSourcePak(String systemCode, String apiName);

    FcSourcePak getSourcePak(String systemCode);

    String getAccessToken(String systemCode);

}
