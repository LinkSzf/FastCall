package priv.szf.fastcall.api.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class FcRetryDTO {

    private Long id;

    @NotNull(message = "最大重试次数不能为空")
    @Min(value = 0, message = "最大重试次数不能小于0")
    private Integer attempts;

    @NotNull(message = "重试间隔不能为空")
    @Min(value = 0, message = "重试间隔不能小于0")
    private Long duration;

}
