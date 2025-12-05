package priv.szf.fastcall.core.call;

import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcClientSettingPak;
import priv.szf.fastcall.core.model.FcSystemPak;

import java.util.Objects;

public final class FcUtils {

    private FcUtils() {}

    public static String calculateClientKey(FcSystemPak system, FcApiPak apiPak) {
        FcClientSettingPak systemSetting = system.getClientSetting();
        FcClientSettingPak apiSetting = apiPak.getClientSetting();
        if (apiSetting.isAllEmpty() || systemSetting.isAllEmpty()) {
            return system.getCode();
        }

        if (!Objects.equals(systemSetting.getConnectTimeout(), apiSetting.getConnectTimeout())
                || !Objects.equals(systemSetting.getReadTimeout(), apiSetting.getReadTimeout())
                || !Objects.equals(systemSetting.getWriteTimeout(), apiSetting.getWriteTimeout())) {
            return getApiUniqueCode(system.getCode(), apiPak.getName());
        }

        return system.getCode();
    }

    public static String getApiUniqueCode(String systemCode, String apiName) {
        return StringUtils.joinWith(":", systemCode, apiName);
    }




}
