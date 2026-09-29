package io.github.linkszf.fastcall.api.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class FcRetryDTO {

    private Long id;

    @NotNull(message = "Maximum retry attempts must not be null")
    @Min(value = 0, message = "Maximum retry attempts must not be less than 0")
    private Integer attempts;

    @NotNull(message = "Retry interval must not be null")
    @Min(value = 0, message = "Retry interval must not be less than 0")
    private Long duration;

}
