package priv.szf.fastcall.core.auth.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.model.content.CookieAuthContent;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.core.model.credential.CookieCredential;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcCookieAuthProvider extends FcBaseInteractiveAuthProvider<CookieAuthContent, Object>
        implements IFcAuthProvider {

    private final IFcSource source;

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

    @Override
    protected IFcSource getSource() {
        return source;
    }
}
