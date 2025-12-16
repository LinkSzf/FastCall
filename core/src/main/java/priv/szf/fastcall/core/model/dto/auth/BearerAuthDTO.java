package priv.szf.fastcall.core.model.dto.auth;


import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class BearerAuthDTO extends BaseDynAuthContentDTO {

    private String fixedToken;


}
