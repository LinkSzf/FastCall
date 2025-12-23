package priv.szf.fastcall.core.call.source;

import priv.szf.fastcall.core.model.FcTokenPak;

public interface IFcSource {

    FcSourcePak getSourcePak(String systemCode);

    <T> FcTokenPak<T> getAccessToken(String systemCode);

    <T> void updateAccessToken(String systemCode, FcTokenPak<T> token);

    int getWeight();

    void setNextSource(IFcSource source);

    IFcSource getNextSource();

    default void init() {}
}
