package priv.szf.fastcall.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("fastcall_sys_token")
@Data
public class FcToken {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long sysId;

    private String token;

    private LocalDateTime issuance;

    private LocalDateTime estimatedExpiration;

}
