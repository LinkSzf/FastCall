package priv.szf.fastcall.core.source;

import priv.szf.fastcall.core.model.FcSourcePak;
import priv.szf.fastcall.core.model.credential.ICredential;

public interface IFcSource {

    FcSourcePak getSourcePak(String systemCode);

    void updateCredential(String systemCode, ICredential credential);

}
