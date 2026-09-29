package io.github.linkszf.fastcall.core.auth.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.FcHttpHeader;
import io.github.linkszf.fastcall.common.model.content.CookieAuthContent;
import io.github.linkszf.fastcall.core.FastCallResponse;
import io.github.linkszf.fastcall.core.auth.IFcCredentialProvider;
import io.github.linkszf.fastcall.core.model.credential.CookieCredential;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FcCookieCredentialProvider extends FcBaseInteractiveCredentialProvider<CookieAuthContent, Object>
        implements IFcCredentialProvider {


    @Override
    public FcAuthType getAuthType() {
        return FcAuthType.COOKIE;
    }

    @Override
    protected CookieCredential buildCredential(@NonNull FastCallResponse<Object> response,
                                               @NonNull CookieAuthContent authContent
    ) {
        List<String> cookieHeaders = response.getHeaders(FcHttpHeader.SET_COOKIE.getName());

        return Optional.ofNullable(cookieHeaders)
                .filter(CollectionUtil::isNotEmpty)
                .map(headers -> String.join("; ", headers))
                .map(CookieCredential::create)
                .orElse(null);
    }

}
