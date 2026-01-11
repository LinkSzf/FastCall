package priv.szf.fastcall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.common.model.content.ApiKeyAuthContent;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.content.BasicAuthContent;
import priv.szf.fastcall.common.model.content.CookieAuthContent;
import priv.szf.fastcall.common.model.content.DigestAuthContent;
import priv.szf.fastcall.common.model.content.NoneAuthContent;
import priv.szf.fastcall.common.model.content.TokenAuthContent;

@Getter
@AllArgsConstructor
public enum FcAuthType {

    NONE(NoneAuthContent.class),

    APIKEY(ApiKeyAuthContent.class),

    BASIC(BasicAuthContent.class, "Basic "),

    DIGEST(DigestAuthContent.class, "Digest "),

    BEARER(TokenAuthContent.class, "Bearer "),

    COOKIE(CookieAuthContent.class);

    FcAuthType(Class<? extends BaseAuthContent> clazz) {
        this(clazz, null);
    }

    private final Class<? extends BaseAuthContent> clazz;

    private final String prefix;

}
