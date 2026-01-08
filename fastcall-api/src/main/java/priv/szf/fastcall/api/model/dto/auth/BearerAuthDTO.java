package priv.szf.fastcall.api.model.dto.auth;


import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class BearerAuthDTO extends BaseDynAuthContentDTO {

    private String tokenField;

    private String expiredInField;

    private String issuanceField;


}
