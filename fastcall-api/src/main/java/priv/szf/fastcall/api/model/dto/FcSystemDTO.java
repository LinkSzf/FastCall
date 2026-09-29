package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcSystemDTO {

    private Long id;

    @NotNull(message = "Name must not be null")
    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters")
    private String name;

    @NotNull(message = "System code must not be null")
    @Size(min = 1, max = 30, message = "System code length must be between 1 and 30 characters")
    private String code;

    @NotNull(message = "Enable flag must not be null")
    private Boolean enable;

    @NotNull(message = "Host address must not be null")
    @Size(min = 1, max = 50, message = "Host address length must be between 1 and 100 characters")
    private String host;

    @NotNull(message = "Auth type must not be null")
    private FcAuthType authType;

    @Min(value = 1, message = "Connect timeout must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Connect timeout is too large")
    private Integer connectTimeout;

    @Min(value = 1, message = "Read timeout must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Read timeout is too large")
    private Integer readTimeout;

    @Min(value = 1, message = "Write timeout must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Write timeout is too large")
    private Integer writeTimeout;

    @Size(min = 1, max = 200, message = "Description length must be between 1 and 200 characters")
    private String description;

    @Valid
    private FcAuthDTO auth;

    @Valid
    private FcRetryDTO retry;
}
