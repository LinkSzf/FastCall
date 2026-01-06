package priv.szf.fastcall.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuthContent extends BaseAuthContent {

    private String key;

    private String value;

    private In addTo;

    public enum In {
        HEADER,
        QUERY
    }
}
