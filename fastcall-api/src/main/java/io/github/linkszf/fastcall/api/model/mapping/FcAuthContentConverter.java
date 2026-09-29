package io.github.linkszf.fastcall.api.model.mapping;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import io.github.linkszf.fastcall.api.model.dto.auth.ApiKeyAuthDTO;
import io.github.linkszf.fastcall.api.model.dto.auth.BaseAuthContentDTO;
import io.github.linkszf.fastcall.api.model.dto.auth.BasicAuthDTO;
import io.github.linkszf.fastcall.api.model.dto.auth.BearerAuthDTO;
import io.github.linkszf.fastcall.api.model.dto.auth.CookieAuthDTO;
import io.github.linkszf.fastcall.api.model.dto.auth.DigestAuthDTO;
import io.github.linkszf.fastcall.common.model.content.ApiKeyAuthContent;
import io.github.linkszf.fastcall.common.model.content.BaseAuthContent;
import io.github.linkszf.fastcall.common.model.content.BasicAuthContent;
import io.github.linkszf.fastcall.common.model.content.CookieAuthContent;
import io.github.linkszf.fastcall.common.model.content.TokenAuthContent;
import io.github.linkszf.fastcall.common.model.content.DigestAuthContent;
import io.github.linkszf.fastcall.data.entity.FcAuth;

import java.util.Map;

@Component
public class FcAuthContentConverter {

    private static final Map<Class<? extends BaseAuthContent>, Class<? extends BaseAuthContentDTO>> MAP
            = MapBuilder.<Class<? extends BaseAuthContent>, Class<? extends BaseAuthContentDTO>>create()
            .put(ApiKeyAuthContent.class, ApiKeyAuthDTO.class)
            .put(BasicAuthContent.class, BasicAuthDTO.class)
            .put(TokenAuthContent.class, BearerAuthDTO.class)
            .put(DigestAuthContent.class, DigestAuthDTO.class)
            .put(CookieAuthContent.class, CookieAuthDTO.class)
            .build();

    public BaseAuthContentDTO toBean(FcAuth entity){
        Class<? extends BaseAuthContent> clazz = entity.getType().getClazz();
        return JSONUtil.toBean(entity.getContent(), MAP.get(clazz));
    }

    public String toJson(BaseAuthContentDTO content){
        return JSONUtil.toJsonStr(content);
    }



}
