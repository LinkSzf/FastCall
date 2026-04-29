package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.api.model.dto.auth.BaseAuthContentDTO;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcAuthDTO {

    private Long id;

    @NotNull(message = "认证类型不能为空")
    private FcAuthType type;

    @NotNull(message = "认证路径不能为空")
    @Size(min = 1, max = 200, message = "认证路径长度必须在1到200个字符之间")
    private String path;

    @Valid
    @NotNull(message = "认证信息不能为空")
    private BaseAuthContentDTO content;

    @Min(value = 1, message = "授权过期时间不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "授权过期时间过大")
    private Integer expiration;

    @Size(min = 1, max = 100, message = "指定主机长度必须在1到100个字符之间")
    private String particularHost;

    @Min(value = 100, message = "认证失效状态码不能小于100")
    @Max(value = 999, message = "认证失效状态码不能大于999")
    private Integer statusCode;

}
