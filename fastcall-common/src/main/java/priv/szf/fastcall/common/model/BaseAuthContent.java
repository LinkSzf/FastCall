package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;

@Data
public abstract class BaseAuthContent {

    private FcAuthType type;

}
