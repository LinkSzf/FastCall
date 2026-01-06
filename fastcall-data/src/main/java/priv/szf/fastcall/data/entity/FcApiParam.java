package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import priv.szf.fastcall.common.FcParamPos;

@TableName("fastcall_sys_api_param")
@Data
public class FcApiParam {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long apiId;

    private String name;

    private FcParamPos position;

    private Boolean jsonObj;

    private String defaultValue;

}
