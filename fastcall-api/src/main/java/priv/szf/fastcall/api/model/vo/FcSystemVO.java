package priv.szf.fastcall.api.model.vo;

import lombok.Data;
import priv.szf.fastcall.common.FcFuncScope;

import java.util.Set;


@Data
public class FcSystemVO {

    private String id;

    private String name;

    private String code;

    private Boolean enable;

    private Set<FcFuncScope> scope;

    private String host;

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    private String description;

    private FcAuthVO auth;

}
