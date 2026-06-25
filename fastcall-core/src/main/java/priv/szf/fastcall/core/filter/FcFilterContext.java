package priv.szf.fastcall.core.filter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import priv.szf.fastcall.common.FcCallType;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.FastCallResponse;

import java.time.LocalDateTime;

@Builder
@Getter
public class FcFilterContext {

    private final String system;

    private final String apiName;

    private final FcSourcePak sourcePak;

    private final boolean auth;

    private final String url;

    private final FcCallType callType;

    private final LocalDateTime currentTime;

    @Setter
    private FastCallResponse<?> response;

    @Setter
    private Exception exception;

}
