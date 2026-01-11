package priv.szf.fastcall.core.model.credential;

import okhttp3.Credentials;
import priv.szf.fastcall.common.model.credential.ICredential;

public class BasicCredential implements ICredential {

    private final String auth;

    private BasicCredential(String auth) {
        this.auth = auth;
    }

    public static BasicCredential create(String username, String password) {
        String basic = Credentials.basic(username, password);
        return new BasicCredential(basic);
    }

    @Override
    public String getAuthString() {
        return auth;
    }

    @Override
    public boolean isInvalid() {
        return false;
    }
}
