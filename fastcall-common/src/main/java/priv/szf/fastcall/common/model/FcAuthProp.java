package priv.szf.fastcall.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FcAuthProp implements Serializable {

    private static final long serialVersionUID = -7225850835211481490L;

    private Map<String, String> headers;

    private Map<String, String> params;

    private Object body;

}
