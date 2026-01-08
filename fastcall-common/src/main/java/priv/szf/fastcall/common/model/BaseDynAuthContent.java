package priv.szf.fastcall.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseDynAuthContent extends BaseAuthContent {

    private FcAuthProp prop;

}
