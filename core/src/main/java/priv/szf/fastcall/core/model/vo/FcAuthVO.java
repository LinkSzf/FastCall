package priv.szf.fastcall.core.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import java.time.LocalDateTime;

@Data
public class FcAuthVO {

    private Long id;

    private Long sysId;

    private AuthType type;

    private BaseAuthContent content;

    private Integer expiration;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    private String particularHost;
}
