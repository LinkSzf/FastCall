package priv.szf.fastcall.api.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuthDTO extends BaseAuthContentDTO {

    @NotNull(message = "Key must not be null")
    @Size(min = 1, max = 100, message = "Key length must be between 1 and 100 characters")
    private String key;

    @NotNull(message = "Value must not be null")
    @Size(min = 1, max = 100, message = "Value length must be between 1 and 1000 characters")
    private String value;

    @NotNull(message = "positionOn must not be null")
    private In positionOn;

    public enum In {
        QUERY,
        HEADER
    }

}
