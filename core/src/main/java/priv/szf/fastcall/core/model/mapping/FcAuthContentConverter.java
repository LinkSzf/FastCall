package priv.szf.fastcall.core.model.mapping;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;
import priv.szf.fastcall.core.model.dto.FcAuthDTO;
import priv.szf.fastcall.core.model.entity.FcAuth;

@Component
public class FcAuthContentConverter {

/**
 * 将实体对象转换为对应的Bean对象
 * @param entity 包含认证信息的实体对象
 * @return 转换后的BaseAuthContent对象
 */
    public BaseAuthContent toBean(FcAuth entity){
    // 使用JSON工具类将entity中的content内容按照entity中指定的类型解析为对应的Bean对象
        return JSON.parseObject(entity.getContent(), entity.getType().getClazz());
    }

    public String toJson(BaseAuthContent content){
        return JSON.toJSONString(content);
    }
}
