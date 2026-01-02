package priv.szf.fastcall.core.call.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.model.auth.credential.BasicCredential;
import priv.szf.fastcall.core.model.auth.BasicAuthContent;

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
