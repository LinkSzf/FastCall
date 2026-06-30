package priv.szf.fastcall.core.auth.provider;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.core.model.credential.BasicCredential;
import priv.szf.fastcall.common.model.content.BasicAuthContent;
import priv.szf.fastcall.core.source.FcSourceDelegate;

@RequiredArgsConstructor
@Component
public class FcBasicCredentialProvider extends FcBaseCredentialProvider<BasicAuthContent>
        implements IFcCredentialProvider {

    @Getter
    private final FcSourceDelegate source;

    @Override
    protected BasicCredential buildCredential(@NonNull BasicAuthContent content) {
        return BasicCredential.create(content.getUsername(), content.getPassword());
    }


}
