package priv.szf.fastcall.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TokenAuthContent extends BaseDynAuthContent implements Serializable {

    private static final long serialVersionUID = 5875136221740716126L;
}
