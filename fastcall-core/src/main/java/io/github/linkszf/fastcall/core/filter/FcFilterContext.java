package io.github.linkszf.fastcall.core.filter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.FcCallType;
import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.core.FastCallResponse;

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

    private final FcAuthType authType;

    private final LocalDateTime currentTime;

    @Setter
    private FastCallResponse<?> response;

}
