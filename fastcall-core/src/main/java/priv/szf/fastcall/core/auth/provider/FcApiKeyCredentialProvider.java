package priv.szf.fastcall.core.auth.provider;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthPosition;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.common.model.content.ApiKeyAuthContent;
import priv.szf.fastcall.core.model.credential.ApiKeyCredential;
import priv.szf.fastcall.core.source.FcSourceDelegate;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class FcApiKeyCredentialProvider extends FcBaseCredentialProvider<ApiKeyAuthContent>
        implements IFcCredentialProvider {

    @Getter
    private final FcSourceDelegate source;

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
