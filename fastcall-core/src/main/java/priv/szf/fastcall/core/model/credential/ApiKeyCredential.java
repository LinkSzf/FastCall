package priv.szf.fastcall.core.model.credential;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import priv.szf.fastcall.common.model.ApiKeyAuthContent;

@Getter
public class ApiKeyCredential implements ICredential {

    private final String key;
    private final String value;
    private final ApiKeyAuthContent.In addTo;


    private ApiKeyCredential(String key, String value, ApiKeyAuthContent.In addTo) {
        this.key = key;
        this.value = value;
        this.addTo = addTo;
    }

    public static ApiKeyCredential create(String key, String value, ApiKeyAuthContent.In addTo) {
        return new ApiKeyCredential(key, value, addTo);
    }

    @Override
    public String getAuthString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isInvalid() {
        return false;
    }
}
