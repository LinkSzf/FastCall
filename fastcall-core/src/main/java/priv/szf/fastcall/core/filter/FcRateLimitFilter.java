package priv.szf.fastcall.core.filter;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.FcTimeSpan;
import priv.szf.fastcall.common.exception.FcRateLimitedException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.model.FcRateLimitPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.utils.FcTimeUtil;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.core.filter.support.FcRateLimitSupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Order(100)
public class FcRateLimitFilter implements FcFilter {

    private final FcRateLimitSupport support;

    @Override
    public void init() {
        this.support.init();
    }

    @Override
    public void doFilter(FcFilterContext context, FcFilterChain chain) {
        Long systemId = getSystemId(context);
        List<FcRateLimitPak> rateLimits = this.support.getLimits(systemId);
        if (CollectionUtil.isEmpty(rateLimits)) {
            chain.doFilter(context);
            return;
        }

        checkWithLimits(rateLimits, context.getCurrentTime());

        chain.doFilter(context);

        updateLimits(context, systemId);
    }

    private void updateLimits(FcFilterContext context, Long systemId) {
        Optional.of(context)
                .map(FcFilterContext::getResponse)
                .filter(FastCallResponse::isConnected)
                .filter(r -> !r.isCached())
                .map(FastCallResponse::getRequestTime)
                .ifPresent(requestTime -> this.support.updateLimits(systemId, requestTime));
    }

    private void checkWithLimits(List<FcRateLimitPak> rateLimits, LocalDateTime now) {
        for (FcRateLimitPak rateLimit : rateLimits) {
            LocalDateTime time = rateLimit.getLastTime();
            if (Objects.isNull(time)) {
                continue;
            }

            FcTimeSpan span = rateLimit.getSpan();
            boolean inSameTimeSpan = FcTimeUtil.isInSameTimeSpan(span, time, now);
            if (!inSameTimeSpan) {
                rateLimit.setLastTime(null);
                continue;
            }

            long current = rateLimit.getCurrentCount();
            long limit = rateLimit.getMaximum();
            if (current >= limit) {
                long systemId = rateLimit.getSystemId();
                throw new FcRateLimitedException("系统请求频率超出限制，systemId: {}, span: {}, limit: {}, time: {}",
                        systemId, span, limit, time);
            }
        }
    }

    private Long getSystemId(FcFilterContext context) {
        return Optional.of(context)
                .map(FcFilterContext::getSourcePak)
                .map(FcSourcePak::getSystem)
                .map(FcSystemPak::getId)
                .orElseThrow(() -> new FcUnexpectedException("SystemId not found"));
    }


}
