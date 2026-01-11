package priv.szf.fastcall.api.model.mapping;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.api.model.dto.auth.ApiKeyAuthDTO;
import priv.szf.fastcall.api.model.dto.auth.BaseAuthContentDTO;
import priv.szf.fastcall.api.model.dto.auth.BasicAuthDTO;
import priv.szf.fastcall.api.model.dto.auth.BearerAuthDTO;
import priv.szf.fastcall.api.model.dto.auth.CookieAuthDTO;
import priv.szf.fastcall.api.model.dto.auth.DigestAuthDTO;
import priv.szf.fastcall.api.model.dto.auth.NoneAuthDTO;
import priv.szf.fastcall.common.model.content.ApiKeyAuthContent;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.content.BasicAuthContent;
import priv.szf.fastcall.common.model.content.CookieAuthContent;
import priv.szf.fastcall.common.model.content.TokenAuthContent;
import priv.szf.fastcall.common.model.content.DigestAuthContent;
import priv.szf.fastcall.common.model.content.NoneAuthContent;
import priv.szf.fastcall.data.entity.FcAuth;

import java.util.Map;

@Component
public class FcAuthContentConverter {

    private static final Map<Class<? extends BaseAuthContent>, Class<? extends BaseAuthContentDTO>> MAP
            = MapBuilder.<Class<? extends BaseAuthContent>, Class<? extends BaseAuthContentDTO>>create()
            .put(NoneAuthContent.class, NoneAuthDTO.class)
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
