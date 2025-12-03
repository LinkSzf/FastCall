package priv.szf.fastcall.core.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FcSystemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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
