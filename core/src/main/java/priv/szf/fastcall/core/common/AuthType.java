package priv.szf.fastcall.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.core.model.auth.*;

@Getter
@AllArgsConstructor
public enum AuthType {

    NONE(NoneAuth.class),

    APIKEY(ApiKeyAuth.class),

    JWT(JwtTokenAuth.class, "Bearer "),

    BASIC(BasicAuth.class, "Basic "),

    BEARER(BearerTokenAuth.class, "Bearer ");

    AuthType(Class<? extends BaseAuthContent> clazz) {
        this(clazz, null);
    }

    private final Class<? extends BaseAuthContent> clazz;

    private final String prefix;

    public static boolean isRefreshable(AuthType authType) {
        return authType == JWT
                || authType == BEARER;
    }
}
