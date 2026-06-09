package priv.szf.fastcall.core.model.credential;

import lombok.AllArgsConstructor;
import okhttp3.Credentials;
import priv.szf.fastcall.common.model.credential.ICredential;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BasicCredential extends BaseCredential implements ICredential {

    private final String auth;


    public static BasicCredential create(String username, String password) {
        String basic = Credentials.basic(username, password);
        return new BasicCredential(basic);
    }

    @Override
    public String getAuthString() {
        return auth;
    }

}
