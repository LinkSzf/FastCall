package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;

import java.time.LocalDateTime;

@TableName("fastcall_sys_auth")
@Data
public class FcAuth {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long sysId;

    private FcAuthType type;

    private String path;

    private String content;

    private Integer expiration;

    private LocalDateTime lastAccessTime;

    private String particularHost;

}
