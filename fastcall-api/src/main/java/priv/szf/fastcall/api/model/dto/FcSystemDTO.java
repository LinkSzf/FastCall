package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcFuncScope;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class FcSystemDTO {

    private Long id;

    @NotNull(message = "名称不能为空")
    @Size(min = 1, max = 50, message = "名称长度必须在1到50个字符之间")
    private String name;

    @NotNull(message = "系统识别代码不能为空")
    @Size(min = 1, max = 30, message = "系统识别代码长度必须在1到30个字符之间")
    private String code;

    @NotNull(message = "是否启用不能为空")
    private Boolean enable;

    private Set<FcFuncScope> scope;

    @NotNull(message = "主机地址不能为空")
    @Size(min = 1, max = 50, message = "主机地址长度必须在1到100个字符之间")
    private String host;

    @Min(value = 1, message = "连接超时不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "连接超时过大")
    private Integer connectTimeout;

    @Min(value = 1, message = "读取超时不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "读取超时过大")
    private Integer readTimeout;

    @Min(value = 1, message = "写入超时不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "写入超时过大")
    private Integer writeTimeout;

    @Size(min = 1, max = 200, message = "描述长度必须在1到200个字符之间")
    private String description;

    @Valid
    private FcAuthDTO auth;
}
