package io.github.linkszf.fastcall.api.model.vo;

import lombok.Data;
import io.github.linkszf.fastcall.common.FcParamPos;

@Data
public class FcApiParamVO {

    private String id;

    private String apiId;

    private String name;

    private FcParamPos position;

    private Boolean jsonObj;

    private String defaultValue;

}
