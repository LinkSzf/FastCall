package priv.szf.fastcall.api.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class DigestAuthDTO extends BaseAuthContentDTO {

    @Size(min = 1, max = 100, message = "用户名长度必须在1到100个字符之间")
    @NotNull(message = "用户名不能为空")
    private String username;

    @Size(min = 1, max = 100, message = "密码长度必须在1到100个字符之间")
    @NotNull(message = "密码不能为空")
    private String password;

}
