package io.github.linkszf.fastcall.core.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import io.github.linkszf.fastcall.core.FastCallClient;
import io.github.linkszf.fastcall.core.filter.FcFilterContext;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FcFilterContextSupport {


    public static <T> FcFilterContext createFilterContext(FastCallClient.Builder<T> builder, String system) {
        return FcFilterContext.builder()
                .system(system)
                .apiName(builder.getApiName())
                .auth(builder.isAuth())
                .url(builder.getFullUrl())
                .callType(builder.getCallType())
                .authType(builder.getAuthType())
                .sourcePak(builder.getSourcePak())
                .currentTime(LocalDateTime.now())
                .build();
    }

}
