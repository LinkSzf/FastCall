package priv.szf.fastcall.core.auth;

import priv.szf.fastcall.common.model.credential.ICredential;

public interface IFcAuthProvider {

    ICredential getCredential(String system);

}
