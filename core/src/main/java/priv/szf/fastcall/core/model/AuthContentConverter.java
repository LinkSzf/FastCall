package priv.szf.fastcall.core.model;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.entity.FcAuth;

@Component
public class AuthContentConverter {

    public BaseAuthContent toBean(FcAuth entity){
        return JSON.parseObject(entity.getContent(), entity.getType().getClazz());
    }

}
