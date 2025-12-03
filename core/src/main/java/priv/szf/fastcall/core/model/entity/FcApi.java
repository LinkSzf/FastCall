package priv.szf.fastcall.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("fastcall_sys_api")
@Data
public class FcApi {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long sysId;

    private String name;

    private String path;

    private String method;

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    private LocalDateTime lastAccessTime;

    private String particularHost;

    private String description;
}
