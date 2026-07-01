package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.model.content.CookieAuthContent;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.auth.IFcCredentialProvider;
import priv.szf.fastcall.core.model.credential.CookieCredential;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
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
