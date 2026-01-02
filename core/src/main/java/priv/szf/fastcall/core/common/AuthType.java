package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.core.model.auth.*;

@Getter
@AllArgsConstructor
public enum AuthType {

    NONE(NoneAuthContent.class),

    APIKEY(ApiKeyAuthContent.class),

    BASIC(BasicAuthContent.class, "Basic "),

    DIGEST(DigestAuthContent.class, "Digest "),

    BEARER(TokenAuthContent.class, "Bearer ");

    AuthType(Class<? extends BaseAuthContent> clazz) {
        this(clazz, null);
    }

    private final Class<? extends BaseAuthContent> clazz;

    private final String prefix;

}
