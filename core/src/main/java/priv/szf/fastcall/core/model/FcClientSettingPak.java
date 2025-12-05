package priv.szf.fastcall.core.model;

import cn.hutool.core.util.ObjUtil;
import lombok.Data;


@Data
public class FcClientSettingPak {

    private Integer connectTimeout;

    private Integer readTimeout;

    private Integer writeTimeout;

    public boolean isAllEmpty() {
        return ObjUtil.isAllEmpty(connectTimeout, readTimeout, writeTimeout);
    }
}
