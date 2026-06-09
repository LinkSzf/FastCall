package priv.szf.fastcall.core.model.credential;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import priv.szf.fastcall.common.FcAuthPosition;
import priv.szf.fastcall.common.model.credential.ICredential;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class ApiKeyCredential extends BaseCredential implements ICredential {

    private final String key;
    private final String value;
    private final FcAuthPosition positionOn;

    public static ApiKeyCredential create(String key, String value, FcAuthPosition positionOn) {
        return new ApiKeyCredential(key, value, positionOn);
    }

    @Override
    public String getAuthString() {
        return StrUtil.EMPTY;
    }

}
