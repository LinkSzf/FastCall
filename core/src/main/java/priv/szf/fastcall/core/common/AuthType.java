package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.auth.ApiKeyAuth;
import priv.szf.fastcall.core.model.auth.BasicAuth;
import priv.szf.fastcall.core.model.auth.BearerAuth;
import priv.szf.fastcall.core.model.auth.NoneAuth;
import priv.szf.fastcall.core.model.auth.TokenAuth;

@Getter
@AllArgsConstructor
public enum AuthType {

    NONE(NoneAuth.class, null),

    APIKEY(ApiKeyAuth.class, null),

    TOKEN(TokenAuth.class, null),

    BASIC(BasicAuth.class, "Basic "),

    BEARER(BearerAuth.class, null);

    private final Class<? extends BaseAuthContent> clazz;

    private final String prefix;

}
