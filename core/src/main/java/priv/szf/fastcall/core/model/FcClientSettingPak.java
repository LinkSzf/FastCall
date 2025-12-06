package priv.szf.fastcall.core.model;

import lombok.Data;

import java.util.Objects;

@Data
public class FcClientSettingPak {

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    public void completeWith(FcClientSettingPak candidate) {
        if (Objects.isNull(getConnectTimeout())) {
            setConnectTimeout(candidate.getConnectTimeout());
        }
        
        if (Objects.isNull(getReadTimeout())) {
            setReadTimeout(candidate.getReadTimeout());
        }
        
        if (Objects.isNull(getWriteTimeout())) {
            setWriteTimeout(candidate.getWriteTimeout());
        }
    }
}
