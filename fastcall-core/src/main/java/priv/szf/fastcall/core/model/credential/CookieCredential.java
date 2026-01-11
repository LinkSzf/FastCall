package priv.szf.fastcall.core.model.credential;


import lombok.Getter;
import priv.szf.fastcall.common.model.credential.ICredential;

@Getter
public class CookieCredential implements ICredential {

    private final String cookie;

    private CookieCredential(String cookie) {
        this.cookie = cookie;
    }

    public static CookieCredential create(String cookie) {
        return new CookieCredential(cookie);
    }

    @Override
    public String getAuthString() {
        return this.cookie;
    }

    @Override
    public boolean isInvalid() {
        return false;
    }
}
