package priv.szf.fastcall.core.model.credential;

import priv.szf.fastcall.common.model.credential.ICredential;

/**
 * Base credential that tracks a one-way invalid flag and leaves validity checks to its subclasses.
 * {@code isInvalid()} is final and returns true once invalidated or when {@code isValid()} is false.
 */
public abstract class BaseCredential implements ICredential {

    private volatile boolean invalid = false;

    @Override
    public final boolean isInvalid() {
        return this.invalid || !isValid();
    }

    @Override
    public void invalidate() {
        this.invalid = true;
    }

    public boolean isValid() {
        return true;
    }
}
