package priv.szf.fastcall.core.call.source;

import priv.szf.fastcall.core.model.auth.credential.ICredential;

public interface IFcSource {

    FcSourcePak getSourcePak(String systemCode);

    void updateCredential(String systemCode, ICredential credential);

}
