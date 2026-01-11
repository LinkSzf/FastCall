package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.model.credential.BasicCredential;
import priv.szf.fastcall.common.model.content.BasicAuthContent;

@RequiredArgsConstructor
@Component
public class FcBasicAuthProvider extends FcBaseAuthProvider<BasicAuthContent>
        implements IFcAuthProvider<BasicAuthContent> {

    private final IFcSource source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected BasicCredential buildCredential(@NonNull BasicAuthContent content) {
        return BasicCredential.create(content.getUsername(), content.getPassword());
    }


}
