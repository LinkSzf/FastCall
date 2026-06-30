package priv.szf.fastcall.core.auth;

import priv.szf.fastcall.common.model.credential.ICredential;

public interface IFcCredentialProvider {


    ICredential buildCredential(FcRequestContext context);

}
