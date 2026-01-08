package priv.szf.fastcall.core.auth.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcHttpHeader;
import priv.szf.fastcall.common.model.CookieAuthContent;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.auth.IFcAuthProvider;
import priv.szf.fastcall.core.model.credential.CookieCredential;
import priv.szf.fastcall.core.source.IFcSource;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class FcCookieAuthProvider extends FcBaseInteractiveAuthProvider<CookieAuthContent, Object>
        implements IFcAuthProvider<CookieAuthContent> {

    private final IFcSource source;

    @Override
    protected CookieCredential buildCredential(@NonNull FastCallResponse<Object> response,
                                               @NonNull CookieAuthContent authContent
    ) {
        String cookie = response.getHeader(FcHttpHeader.SET_COOKIE.getName());
        return Optional.ofNullable(cookie)
                .map(CookieCredential::create)
                .orElse(null);
    }

    @Override
    protected IFcSource getSource() {
        return source;
    }
}
