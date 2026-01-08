package priv.szf.fastcall.api.model.vo;

import lombok.Data;


@Data
public class FcSystemVO {

    private String id;

    private String name;

    private String code;

    private Boolean enable;

    private String host;

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    private String description;

    private FcAuthVO auth;

}
