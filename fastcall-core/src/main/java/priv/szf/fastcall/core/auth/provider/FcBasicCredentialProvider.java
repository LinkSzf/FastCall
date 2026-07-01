package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.core.model.credential.BasicCredential;
import priv.szf.fastcall.common.model.content.BasicAuthContent;

@RequiredArgsConstructor
@Component
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
