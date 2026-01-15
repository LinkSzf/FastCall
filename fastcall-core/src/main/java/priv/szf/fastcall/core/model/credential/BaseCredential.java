package priv.szf.fastcall.core.model.credential;

import priv.szf.fastcall.common.model.credential.ICredential;

public abstract class BaseCredential implements ICredential {

    private boolean invalid = false;

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public void invalidate() {
        this.invalid = true;
    }
}
