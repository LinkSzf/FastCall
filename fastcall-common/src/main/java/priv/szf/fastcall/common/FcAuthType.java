package priv.szf.fastcall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.common.model.ApiKeyAuthContent;
import priv.szf.fastcall.common.model.BaseAuthContent;
import priv.szf.fastcall.common.model.BasicAuthContent;
import priv.szf.fastcall.common.model.DigestAuthContent;
import priv.szf.fastcall.common.model.NoneAuthContent;
import priv.szf.fastcall.common.model.TokenAuthContent;

@Getter
@AllArgsConstructor
public enum FcAuthType {

    NONE(NoneAuthContent.class),

    APIKEY(ApiKeyAuthContent.class),

    BASIC(BasicAuthContent.class, "Basic "),

    DIGEST(DigestAuthContent.class, "Digest "),

    BEARER(TokenAuthContent.class, "Bearer ");

    FcAuthType(Class<? extends BaseAuthContent> clazz) {
        this(clazz, null);
    }

    private final Class<? extends BaseAuthContent> clazz;

    private final String prefix;

}
