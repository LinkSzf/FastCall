package priv.szf.fastcall.api.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class BasicAuthDTO extends BaseAuthContentDTO {

    @Size(min = 1, max = 1000, message = "Username length must be between 1 and 1000 characters")
    @NotNull(message = "Username must not be null")
    private String username;

    @Size(min = 1, max = 1000, message = "Password length must be between 1 and 1000 characters")
    @NotNull(message = "Password must not be null")
    private String password;

}
