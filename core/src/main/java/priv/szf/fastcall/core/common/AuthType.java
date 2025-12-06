package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.core.model.auth.*;

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
