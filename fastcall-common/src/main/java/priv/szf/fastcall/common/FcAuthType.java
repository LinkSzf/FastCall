package priv.szf.fastcall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import priv.szf.fastcall.common.model.content.ApiKeyAuthContent;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.content.BasicAuthContent;
import priv.szf.fastcall.common.model.content.CookieAuthContent;
import priv.szf.fastcall.common.model.content.DigestAuthContent;
import priv.szf.fastcall.common.model.content.JwtAuthContent;
import priv.szf.fastcall.common.model.content.TokenAuthContent;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public enum FcAuthType {

    NONE,

    APIKEY(ApiKeyAuthContent.class),

    BASIC(BasicAuthContent.class, "Basic "),

    DIGEST(DigestAuthContent.class, "Digest "),

    BEARER(TokenAuthContent.class, "Bearer "),

    COOKIE(CookieAuthContent.class),

    JWT(JwtAuthContent.class, "Bearer ");

    FcAuthType(Class<? extends BaseAuthContent> clazz) {
        this(clazz, null);
    }

    private final Class<? extends BaseAuthContent> clazz;

    private final String prefix;

}
