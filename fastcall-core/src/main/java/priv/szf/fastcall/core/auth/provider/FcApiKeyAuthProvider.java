package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthPosition;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.common.model.content.ApiKeyAuthContent;
import priv.szf.fastcall.core.model.credential.ApiKeyCredential;
import priv.szf.fastcall.core.source.FcSourceDelegate;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FcApiKeyAuthProvider extends FcBaseAuthProvider<ApiKeyAuthContent>
        implements IFcAuthProvider {

    private final FcSourceDelegate source;

    @Override
    protected IFcSource getSource() {
        return source;
    }

    @Override
    protected ApiKeyCredential buildCredential(@NonNull ApiKeyAuthContent content) {
        readyContent(content);
        String key = content.getKey();
        String value = content.getValue();
        FcAuthPosition positionOn = content.getPositionOn();
        return ApiKeyCredential.create(key, value, positionOn);
    }

    private void readyContent(ApiKeyAuthContent content) {
        if (Objects.isNull(content.getPositionOn())) {
            content.setPositionOn(FcAuthPosition.HEADER);
        }
    }

}
