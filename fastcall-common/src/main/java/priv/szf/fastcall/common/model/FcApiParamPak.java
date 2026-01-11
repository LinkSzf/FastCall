package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Builder
@Data
public class FcApiParamPak implements Serializable {

    private static final long serialVersionUID = -2201090540519752028L;

    private Map<String, String> headers;

    private Map<String, String> params;

    private Object body;

    public static FcApiParamPak empty(){
        return FcApiParamPak.builder()
                .headers(Collections.emptyMap())
                .params(Collections.emptyMap())
                .body(null)
                .build();
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }


}
