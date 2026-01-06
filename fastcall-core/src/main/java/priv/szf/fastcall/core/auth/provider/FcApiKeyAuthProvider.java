package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.core.source.IFcSource;
import priv.szf.fastcall.common.model.ApiKeyAuthContent;
import priv.szf.fastcall.core.model.credential.ApiKeyCredential;
import priv.szf.fastcall.core.model.credential.ICredential;

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
