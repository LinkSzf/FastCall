package priv.szf.fastcall.core.model.mapping;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.dto.auth.BaseAuthContentDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;

@Component
public class FcAuthContentConverter {

    public BaseAuthContentDTO toBean(FcAuth entity){
        return JSON.parseObject(entity.getContent(), entity.getType().getClazz());
    }

    public String toJson(BaseAuthContentDTO content){
        return JSON.toJSONString(content);
    }
}
