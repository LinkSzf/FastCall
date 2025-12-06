package priv.szf.fastcall.core.model.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuth extends BaseAuthContent {

    private String key;

    private String value;

    private In addTo;

    public enum In {
        HEADER,
        QUERY
    }
}
