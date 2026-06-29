package priv.szf.fastcall.core.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import priv.szf.fastcall.core.FastCallClient;
import priv.szf.fastcall.core.filter.FcFilterContext;

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
