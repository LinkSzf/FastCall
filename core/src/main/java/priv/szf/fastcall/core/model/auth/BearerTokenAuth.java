package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class BearerTokenAuth extends BaseAuthContent {

    private String fixedToken;

    private Map<String, String> params;

}
