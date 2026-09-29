package priv.szf.fastcall.api.model.dto;

import lombok.Data;
import priv.szf.fastcall.common.FcApiMethod;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FcApiDTO {

    private Long id;

    @NotNull(message = "System ID must not be null")
    private Long sysId;

    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters")
    @NotNull(message = "Name must not be null")
    private String name;

    @Size(min = 1, max = 200, message = "Path length must be between 1 and 200 characters")
    @NotNull(message = "Path must not be null")
    private String path;

    @NotNull(message = "Request method must not be null")
    private FcApiMethod method;

    @Min(value = 1, message = "Connect timeout must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Connect timeout is too large")
    private Integer connectTimeout;

    @Min(value = 1, message = "Read timeout must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Read timeout is too large")
    private Integer readTimeout;

    @Min(value = 1, message = "Write timeout must not be less than 1")
    @Max(value = Integer.MAX_VALUE, message = "Write timeout is too large")
    private Integer writeTimeout;

    @Size(min = 1, max = 50, message = "Particular host length must be between 1 and 100 characters")
    private String particularHost;

    @Size(min = 1, max = 200, message = "Description length must be between 1 and 200 characters")
    private String description;
}
