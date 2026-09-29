package io.github.linkszf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.core.auth.IFcCredentialProvider;
import io.github.linkszf.fastcall.core.model.credential.BasicCredential;
import io.github.linkszf.fastcall.common.model.content.BasicAuthContent;

@RequiredArgsConstructor
public class FcBasicCredentialProvider extends FcBaseCredentialProvider<BasicAuthContent>
        implements IFcCredentialProvider {


    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.BASIC;
    }

    @Override
    protected BasicCredential buildCredential(@NonNull BasicAuthContent content) {
        return BasicCredential.create(content.getUsername(), content.getPassword());
    }


}
