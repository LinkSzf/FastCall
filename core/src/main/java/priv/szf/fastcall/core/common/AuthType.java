package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.auth.ApiKeyAuth;
import priv.szf.fastcall.core.model.auth.BasicAuth;
import priv.szf.fastcall.core.model.auth.BearerAuth;
import priv.szf.fastcall.core.model.auth.NoneAuth;
import priv.szf.fastcall.core.model.auth.TokenAuth;

@AllArgsConstructor
public enum AuthType {

    NONE(NoneAuth.class),

    APIKEY(ApiKeyAuth.class),

    TOKEN(TokenAuth.class),

    BASIC(BasicAuth.class),

    BEARER(BearerAuth.class);

    private final Class<? extends BaseAuthContent> clazz;

    public Class<? extends BaseAuthContent> getClazz() {
        return this.clazz;
    }

}
