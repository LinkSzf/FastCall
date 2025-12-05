package priv.szf.fastcall.core.model.mapping;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.dto.FcAuthDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;

@Component
public class FcAuthContentConverter {

    public BaseAuthContent toBean(FcAuth entity){
        return JSON.parseObject(entity.getContent(), entity.getType().getClazz());
    }

    public String toJson(FcAuthDTO dto){
        return JSON.toJSONString(dto.getContent());
    }
}
