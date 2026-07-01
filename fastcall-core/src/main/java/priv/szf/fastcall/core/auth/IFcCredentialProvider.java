package priv.szf.fastcall.core.auth;

import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.model.credential.ICredential;

public interface IFcCredentialProvider {

    FcAuthType getAuthType();


    ICredential buildCredential(FcRequestContext context);

}
