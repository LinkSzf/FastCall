package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcHeaderAssignDTO {

    private Long id;

    @NotNull(message = "System ID must not be null")
    private Long sysId;

    private Long apiId;

    @NotNull(message = "header name must not be null")
    @Size(min = 1, max = 50, message = "header name length must not exceed 50 characters")
    private String name;

    @Size(min = 1, max = 500, message = "header value length must not exceed 500 characters")
    private String value;

    @NotNull(message = "header operation must not be null")
    private FcHeaderOperation operation;

    @NotNull(message = "header type must not be null")
    private FcHeaderType type;

    private String path;

}
