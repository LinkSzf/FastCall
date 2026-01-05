package priv.szf.fastcall.core.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("fastcall_sys")
@Data
public class FcSystem {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String code;

    private Boolean enable;

    private String host;

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    private String description;

    private LocalDateTime updateAt;

    @TableField(exist = false)
    private FcAuth auth;

}
