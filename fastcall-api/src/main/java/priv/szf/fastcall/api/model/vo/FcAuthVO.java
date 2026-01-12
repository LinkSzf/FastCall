package priv.szf.fastcall.api.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import priv.szf.fastcall.api.model.dto.auth.BaseAuthContentDTO;
import priv.szf.fastcall.common.FcAuthType;

import java.time.LocalDateTime;

@Data
public class FcAuthVO {

    private String id;

    private String sysId;

    private FcAuthType type;

    private String path;

    private BaseAuthContentDTO content;

    private Integer expiration;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    private String particularHost;

    private Integer statusCode;
}
