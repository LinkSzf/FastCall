package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcParamPos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcApiParamDTO {

    private Long id;

    @NotNull(message = "API ID must not be null")
    private Long apiId;

    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters")
    @NotNull(message = "Name must not be null")
    private String name;

    @NotNull(message = "Parameter position must not be null")
    private FcParamPos position;

    @NotNull(message = "Json object flag must not be null")
    private Boolean jsonObj;

    @Size(max = 500, message = "Default value must not exceed 500 characters")
    private String defaultValue;
}
