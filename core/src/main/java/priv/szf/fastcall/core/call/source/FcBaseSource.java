package priv.szf.fastcall.core.call.source;

import priv.szf.fastcall.core.model.FcTokenPak;

public abstract class FcBaseSource implements IFcSource {

    private IFcSource nextSource;

    @Override
    public void setNextSource(IFcSource nextSource) {
        this.nextSource = nextSource;
    }

    @Override
    public IFcSource getNextSource() {
        return nextSource;
    }

    @Override
    public <T> FcTokenPak<T> getAccessToken(String systemCode) {
        return (FcTokenPak<T>) getSourcePak(systemCode).getAccessToken();
    }


}
