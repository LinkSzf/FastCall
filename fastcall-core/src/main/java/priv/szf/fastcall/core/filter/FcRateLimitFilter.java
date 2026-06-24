package priv.szf.fastcall.core.filter;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import priv.szf.fastcall.common.FcTimeSpan;
import priv.szf.fastcall.common.exception.FcRateLimitedException;
import priv.szf.fastcall.common.filter.FcFilter;
import priv.szf.fastcall.common.model.FcRateLimitPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.utils.FcTimeUtil;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.filter.support.FcRateLimitSupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class FcRateLimitFilter implements FcFilter {

    private final FcRateLimitSupport support;

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public void doFilter(FcSourcePak sourcePak) {
        FcSystemPak system = sourcePak.getSystem();
        Long id = system.getId();
        Long systemId = Objects.nonNull(id) ? id : FcUtils.encodeId(system.getCode());
        List<FcRateLimitPak> rateLimits = this.support.getLimits(systemId);
        if (CollectionUtil.isEmpty(rateLimits)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (FcRateLimitPak rateLimit : rateLimits) {
            check(rateLimit, now);
        }

        this.support.calculateAndAddCurrent(systemId, rateLimits, now);
    }

    private void check(FcRateLimitPak rateLimit, LocalDateTime now) {
        LocalDateTime time = rateLimit.getTime();
        if (Objects.isNull(time)) {
            return;
        }

        FcTimeSpan span = rateLimit.getSpan();

        boolean inSameTimeSpan = FcTimeUtil.isInSameTimeSpan(span, time, now);
        if (!inSameTimeSpan) {
            return;
        }

        long current = rateLimit.getCurrent();
        long limit = rateLimit.getMaximum();
        if (current >= limit) {
            Long systemId = rateLimit.getSystemId();
            throw new FcRateLimitedException("系统请求频率超出限制，systemId: {}, span: {}, limit: {}, time: {}",
                    systemId, span, limit, time);
        }
    }


}
