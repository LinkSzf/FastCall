package io.github.linkszf.fastcall.core.model.credential;


import lombok.RequiredArgsConstructor;
import io.github.linkszf.fastcall.common.model.credential.ICredential;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CookieCredential extends BaseCredential implements ICredential {

    private final String cookie;


    public static CookieCredential create(String cookie) {
        return new CookieCredential(cookie);
    }

    @Override
    public String getAuthString() {
        return this.cookie;
    }

}
