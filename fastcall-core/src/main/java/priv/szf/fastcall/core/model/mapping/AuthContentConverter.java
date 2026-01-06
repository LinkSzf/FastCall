package priv.szf.fastcall.core.model.mapping;

import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.model.BaseAuthContent;
import priv.szf.fastcall.data.entity.FcAuth;

@Component
public class AuthContentConverter {

    public BaseAuthContent toBean(FcAuth entity){
        return JSONUtil.toBean(entity.getContent(), entity.getType().getClazz());
    }

}
