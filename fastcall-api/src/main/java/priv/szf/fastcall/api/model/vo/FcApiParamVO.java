package priv.szf.fastcall.api.model.vo;

import lombok.Data;
import priv.szf.fastcall.common.FcParamPos;

@Data
public class FcApiParamVO {

    private Long id;

    private Long apiId;

    private String name;

    private FcParamPos position;

    private Boolean jsonObj;

    private String defaultValue;

}
