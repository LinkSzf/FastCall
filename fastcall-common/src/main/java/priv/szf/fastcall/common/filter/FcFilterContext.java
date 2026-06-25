package priv.szf.fastcall.common.filter;

import lombok.Builder;
import lombok.Data;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.model.FcSourcePak;

@Builder
@Data
public class FcFilterContext {

    private String system;

    private String apiName;

    private boolean auth;

    private String url;

    private FcCallType callType;

    private FcSourcePak sourcePak;

    private Integer responseCode;

    private boolean success;

    private Class<? extends Throwable> exceptionType;

    private Throwable throwable;

}
