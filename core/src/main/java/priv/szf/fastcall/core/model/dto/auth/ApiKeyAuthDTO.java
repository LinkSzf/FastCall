package priv.szf.fastcall.core.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuthDTO extends BaseAuthContentDTO {

    @NotNull(message = "key不能为空")
    private String key;

    @NotNull(message = "value不能为空")
    private String value;

    @NotNull(message = "addTo不能为空")
    private In addTo;

    public enum In {
        QUERY,
        HEADER
    }

}
