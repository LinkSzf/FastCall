package priv.szf.fastcall.core.call.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.IFcAuthProvider;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.model.auth.ApiKeyAuthContent;
import priv.szf.fastcall.core.model.auth.credential.ApiKeyCredential;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

@RequiredArgsConstructor
@Component
public class FcApiKeyAuthProvider extends FcBaseAuthProvider<ApiKeyAuthContent>
        implements IFcAuthProvider<ApiKeyAuthContent> {

    private final IFcSource source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected ICredential buildCredential(@NonNull ApiKeyAuthContent content) {
        return ApiKeyCredential.create(content.getKey(), content.getValue(), content.getAddTo());
    }

}
