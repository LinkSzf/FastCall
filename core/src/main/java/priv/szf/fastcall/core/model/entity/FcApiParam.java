package priv.szf.fastcall.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import priv.szf.fastcall.core.common.FcParamPos;
import priv.szf.fastcall.core.model.IFcApiParam;

@TableName("fastcall_sys_api_param")
@Data
public class FcApiParam implements IFcApiParam {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long apiId;

    private String name;

    private FcParamPos position;

    private Boolean jsonObj;

    private String defaultValue;

}
