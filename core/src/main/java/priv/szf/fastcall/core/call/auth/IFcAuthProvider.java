package priv.szf.fastcall.core.call.auth;

import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

public interface IFcAuthProvider<C extends BaseAuthContent> {

    ICredential getCredential(String system);

}
