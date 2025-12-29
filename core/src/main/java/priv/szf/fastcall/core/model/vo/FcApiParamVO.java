package priv.szf.fastcall.core.model.vo;

import lombok.Data;
import priv.szf.fastcall.core.common.FcParamPos;

@Data
public class FcApiParamVO {

    private Long id;

    private Long apiId;

    private String name;

    private FcParamPos position;

    private Boolean jsonObj;

    private String defaultValue;

}
