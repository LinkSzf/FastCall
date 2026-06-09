package priv.szf.fastcall.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import priv.szf.fastcall.common.FcAuthPosition;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiKeyAuthContent extends BaseAuthContent implements Serializable {

    private static final long serialVersionUID = -7318639860049336078L;

    private String key;

    private String value;

    private FcAuthPosition positionOn;

}
