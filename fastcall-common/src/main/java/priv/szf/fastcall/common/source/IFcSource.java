package priv.szf.fastcall.common.source;


import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.credential.ICredential;

public interface IFcSource {

    FcSourcePak getSourcePak(String system);

    void updateCredential(String system, ICredential credential);

}
