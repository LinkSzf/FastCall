package priv.szf.fastcall.core.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuthDTO extends BaseAuthContentDTO {

    @NotNull(message = "key不能为空")
    @Size(min = 1, max = 100, message = "key长度必须在1到100个字符之间")
    private String key;

    @NotNull(message = "value不能为空")
    @Size(min = 1, max = 100, message = "value长度必须在1到1000个字符之间")
    private String value;

    @NotNull(message = "addTo不能为空")
    private In addTo;

    public enum In {
        QUERY,
        HEADER
    }

}
