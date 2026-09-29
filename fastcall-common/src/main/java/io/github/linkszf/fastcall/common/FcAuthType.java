package io.github.linkszf.fastcall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.github.linkszf.fastcall.common.model.content.ApiKeyAuthContent;
import io.github.linkszf.fastcall.common.model.content.BaseAuthContent;
import io.github.linkszf.fastcall.common.model.content.BasicAuthContent;
import io.github.linkszf.fastcall.common.model.content.CookieAuthContent;
import io.github.linkszf.fastcall.common.model.content.DigestAuthContent;
import io.github.linkszf.fastcall.common.model.content.JwtAuthContent;
import io.github.linkszf.fastcall.common.model.content.TokenAuthContent;

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
