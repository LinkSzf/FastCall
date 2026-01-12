package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcHeaderAssignDTO {

    private Long id;

    @NotNull(message = "系统ID不能为空")
    private Long sysId;

    private Long apiId;

    @NotNull(message = "header名称不能为空")
    @Size(min = 1, max = 50, message = "header名称长度不能大于50个字符")
    private String name;

    @Size(min = 1, max = 500, message = "header值长度不能大于500个字符")
    private String value;

    @NotNull(message = "header操作不能为空")
    private FcHeaderOperation operation;

    @NotNull(message = "header类型不能为空")
    private FcHeaderType type;

    private String path;

}
