package priv.szf.fastcall.core.auth;

import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

public interface IFcAuthProvider<C extends BaseAuthContent> {

    ICredential getCredential(String system);

}
