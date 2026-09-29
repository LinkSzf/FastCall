package io.github.linkszf.fastcall.api.model.dto;

import lombok.Data;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.api.model.dto.auth.BaseAuthContentDTO;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcAuthDTO {

    private Long id;

    @NotNull(message = "Auth type must not be null")
    private FcAuthType type;

    @Size(min = 1, max = 200, message = "Auth path length must be between 1 and 200 characters")
    private String path;

    @Valid
    @NotNull(message = "Auth content must not be null")
    private BaseAuthContentDTO content;

    @Min(value = 1, message = "Auth expiration must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Auth expiration is too large")
    private Integer expiration;

    @Size(min = 1, max = 100, message = "Particular host length must be between 1 and 100 characters")
    private String particularHost;

    @Min(value = 100, message = "Auth failure status code must not be less than 100")
    @Max(value = 999, message = "Auth failure status code must not be greater than 999")
    private Integer unauthorizedCode;

}
