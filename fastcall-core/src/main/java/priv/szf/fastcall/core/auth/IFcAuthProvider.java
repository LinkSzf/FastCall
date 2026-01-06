package priv.szf.fastcall.core.auth;

import priv.szf.fastcall.common.model.BaseAuthContent;
import priv.szf.fastcall.core.model.credential.ICredential;

public interface IFcAuthProvider<C extends BaseAuthContent> {

    ICredential getCredential(String system);

}
