package io.github.linkszf.fastcall.core.filter.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.model.FcRateLimitPak;
import io.github.linkszf.fastcall.common.model.IFcPak;
import io.github.linkszf.fastcall.common.source.IFcPakProvider;
import io.github.linkszf.fastcall.core.FcUtils;
import io.github.linkszf.fastcall.core.config.FastCallProperties;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FcRateLimitSupport {

    private final Map<Long, List<FcRateLimitPak>> cache = new HashMap<>();

    private final IFcPakProvider pakProvider;

    private final FastCallProperties properties;

    public void init() {
        List<FcRateLimitPak> dbLimits = this.pakProvider.getAllRateLimits();
        Set<Long> idSet = dbLimits.stream().map(FcRateLimitPak::getSystemId).collect(Collectors.toSet());
        List<FcRateLimitPak> propertyLimits = this.properties.getEasySource().stream().map(this::toRateLimitPak)
                .flatMap(List::stream)
                .filter(l -> !idSet.contains(l.getSystemId()))
                .collect(Collectors.toList());

        Map<Long, List<FcRateLimitPak>> finalLimitMap = CollectionUtil.union(dbLimits, propertyLimits).stream()
                .filter(l -> l.getMaximum() > 0)
                .peek(IFcPak::init)
                .collect(Collectors.groupingBy(FcRateLimitPak::getSystemId));

        this.cache.putAll(finalLimitMap);
    }

    public List<FcRateLimitPak> getLimits(Long systemId) {
        return this.cache.getOrDefault(systemId, Collections.emptyList());
    }

    @Async(FastCallConsts.ASYNC_EXECUTOR)
    public void updateLimits(Long systemId, LocalDateTime currentTime) {
        List<FcRateLimitPak> rateLimits = getLimits(systemId);
        if (CollectionUtil.isEmpty(rateLimits)) {
            return;
        }

        for (FcRateLimitPak rateLimit : rateLimits) {
            LocalDateTime lastTime = rateLimit.getLastTime();
            boolean hasLastTime = Objects.nonNull(lastTime);
            if (hasLastTime) {
                long current = rateLimit.getCurrentCount();
                current++;
                rateLimit.setCurrentCount(current);
            } else {
                rateLimit.setLastTime(currentTime);
                rateLimit.setCurrentCount(1);
            }
        }

        this.pakProvider.saveRateLimits(systemId, rateLimits);
    }

    private List<FcRateLimitPak> toRateLimitPak(FastCallProperties.EasySource easySource) {
        Long systemId = Optional.of(easySource)
                .map(FastCallProperties.EasySource::getSystem)
                .map(FastCallProperties.EasySource.System::getCode)
                .map(FcUtils::encodeId)
                .orElse(null);
        if (Objects.isNull(systemId)) {
            return Collections.emptyList();
        }

        return easySource.getRateLimits().stream()
                .map(r ->
                        FcRateLimitPak.builder()
                            .id(IdUtil.getSnowflakeNextId())
                            .systemId(systemId)
                            .span(r.getSpan())
                            .maximum(r.getMaximum())
                            .build()
                )
                .collect(Collectors.toList());
    }


}
