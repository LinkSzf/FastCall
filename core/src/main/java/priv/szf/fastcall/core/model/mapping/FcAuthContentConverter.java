package priv.szf.fastcall.core.model.mapping;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.auth.ApiKeyAuth;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.auth.BasicAuth;
import priv.szf.fastcall.core.model.auth.BearerTokenAuth;
import priv.szf.fastcall.core.model.auth.JwtTokenAuth;
import priv.szf.fastcall.core.model.auth.NoneAuth;
import priv.szf.fastcall.core.model.dto.auth.ApiKeyAuthDTO;
import priv.szf.fastcall.core.model.dto.auth.BaseAuthContentDTO;
import priv.szf.fastcall.core.model.dto.auth.BasicAuthDTO;
import priv.szf.fastcall.core.model.dto.auth.BearerAuthDTO;
import priv.szf.fastcall.core.model.dto.auth.NoneAuthDTO;
import priv.szf.fastcall.core.model.dto.auth.TokenAuthDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;

import java.util.Map;

@Component
public class FcAuthContentConverter {

    private static final Map<Class<? extends BaseAuthContent>, Class<? extends BaseAuthContentDTO>> MAP
            = MapBuilder.<Class<? extends BaseAuthContent>, Class<? extends BaseAuthContentDTO>>create()
            .put(NoneAuth.class, NoneAuthDTO.class)
            .put(ApiKeyAuth.class, ApiKeyAuthDTO.class)
            .put(BasicAuth.class, BasicAuthDTO.class)
            .put(BearerTokenAuth.class, BearerAuthDTO.class)
            .put(JwtTokenAuth.class, TokenAuthDTO.class)
            .build();

    public BaseAuthContentDTO toBean(FcAuth entity){
        Class<? extends BaseAuthContent> clazz = entity.getType().getClazz();
        return JSONUtil.toBean(entity.getContent(), MAP.get(clazz));
    }

    public String toJson(BaseAuthContentDTO content){
        return JSONUtil.toJsonStr(content);
    }



}
