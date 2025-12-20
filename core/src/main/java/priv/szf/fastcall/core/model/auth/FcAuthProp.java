package priv.szf.fastcall.core.model.auth;

import lombok.Data;

import java.util.Map;

@Data
public class FcAuthProp {

    private Map<String, String> headers;

    private Map<String, String> params;

    private Object body;

}
