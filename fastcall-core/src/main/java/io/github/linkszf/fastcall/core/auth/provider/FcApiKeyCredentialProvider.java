package io.github.linkszf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import io.github.linkszf.fastcall.common.FcAuthPosition;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.core.auth.IFcCredentialProvider;
import io.github.linkszf.fastcall.common.model.content.ApiKeyAuthContent;
import io.github.linkszf.fastcall.core.model.credential.ApiKeyCredential;

import java.util.Objects;

@RequiredArgsConstructor
public class FcApiKeyCredentialProvider extends FcBaseCredentialProvider<ApiKeyAuthContent>
        implements IFcCredentialProvider {


    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.APIKEY;
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
