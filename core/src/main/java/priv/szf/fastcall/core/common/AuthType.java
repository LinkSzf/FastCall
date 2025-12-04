package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import priv.szf.fastcall.core.model.BaseAuthContent;
import priv.szf.fastcall.core.model.dto.ApiKeyAuth;
import priv.szf.fastcall.core.model.dto.BasicAuth;
import priv.szf.fastcall.core.model.dto.BearerAuth;
import priv.szf.fastcall.core.model.dto.NoneAuth;
import priv.szf.fastcall.core.model.dto.TokenAuth;

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
