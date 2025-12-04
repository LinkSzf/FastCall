package priv.szf.fastcall.core.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FcApiVO {

    private Long id;

    private Long sysId;

    private String name;

    private String path;

    private String method;

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    private String particularHost;

    private String description;
}
