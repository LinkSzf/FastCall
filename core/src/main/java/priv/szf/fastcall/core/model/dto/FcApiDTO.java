package priv.szf.fastcall.core.model.dto;

import lombok.Data;
import priv.szf.fastcall.core.common.ApiMethod;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcApiDTO {

    private Long id;

    @NotNull(message = "系统ID不能为空")
    private Long sysId;

    @Size(min = 1, max = 50, message = "名称长度必须在1到50个字符之间")
    private String name;

    @Size(min = 1, max = 200, message = "路径长度必须在1到200个字符之间")
    private String path;

    @NotNull(message = "请求方式不能为空")
    private ApiMethod method;

    @Min(value = 1, message = "连接超时不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "连接超时过大")
    private Integer connectTimeout;

    @Min(value = 1, message = "读取超时不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "读取超时过大")
    private Integer readTimeout;

    @Min(value = 1, message = "写入超时不能小于1")
    @Max(value = Integer.MAX_VALUE, message = "写入超时过大")
    private Integer writeTimeout;

    @Size(min = 1, max = 50, message = "指定主机长度必须在1到100个字符之间")
    private String particularHost;

    @Size(min = 1, max = 200, message = "描述长度必须在1到200个字符之间")
    private String description;
}
