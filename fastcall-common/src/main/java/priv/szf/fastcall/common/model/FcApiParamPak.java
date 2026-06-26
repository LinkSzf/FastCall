package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Builder
@Getter
public class FcApiParamPak implements Serializable {

    private static final long serialVersionUID = -2201090540519752028L;

    private final Map<String, String> headers;

    private final Map<String, String> params;

    private final Object body;


}
