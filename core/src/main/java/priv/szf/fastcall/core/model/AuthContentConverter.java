package priv.szf.fastcall.core.model;

import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.entity.FcAuth;

@Component
public class AuthContentConverter {

    public BaseAuthContent toBean(FcAuth entity){
        return JSONUtil.toBean(entity.getContent(), entity.getType().getClazz());
    }

}
