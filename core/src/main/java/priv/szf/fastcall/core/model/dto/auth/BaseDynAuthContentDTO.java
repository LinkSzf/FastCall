package priv.szf.fastcall.core.model.dto.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;


@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDynAuthContentDTO extends BaseAuthContentDTO {

    private Map<String, String> params;

    private String tokenField;

    private String expiredInField;

    private String issuanceField;

}
