package priv.szf.fastcall.core.model.dto;

import lombok.Data;
import priv.szf.fastcall.core.common.AuthType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class FcAuthDTO {

    private Long id;

    @NotNull(message = "系统id不能为空")
    private Long sysId;

    @Pattern(regexp = "^(NONE|APIKEY|TOKEN|BASIC|BEARER)$", message = "认证类型必须为指定值")
    private AuthType type;

    @NotNull(message = "是否启用不能为空")
    private String content;

    @Min(value = 1, message = "授权过期时间不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "授权过期时间过大")
    private Integer expiration;

    @Size(min = 1, max = 50, message = "指定主机长度必须在1到100个字符之间")
    private String particularHost;

}
