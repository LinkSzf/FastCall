package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcParamPos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcApiParamDTO {

    private Long id;

    @NotNull(message = "apiID不能为空")
    private Long apiId;

    @Size(min = 1, max = 50, message = "名称长度必须在1到50个字符之间")
    @NotNull(message = "名称不能为空")
    private String name;

    @NotNull(message = "参数位置不能为空")
    private FcParamPos position;

    @NotNull(message = "是否为Json对象不能为空")
    private Boolean jsonObj;

    @Size(max = 500, message = "默认值最大不能超过500个字符")
    private String defaultValue;
}
