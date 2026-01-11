package priv.szf.fastcall.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FcClientSettingPak implements Serializable {

    private static final long serialVersionUID = -7562993860194630639L;

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

}
