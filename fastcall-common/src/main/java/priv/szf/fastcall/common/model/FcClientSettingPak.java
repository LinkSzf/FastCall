package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class FcClientSettingPak implements Serializable {

    private static final long serialVersionUID = -7562993860194630639L;

    private final Integer connectTimeout;

    private final Integer readTimeout;

    private final Integer writeTimeout;

}
