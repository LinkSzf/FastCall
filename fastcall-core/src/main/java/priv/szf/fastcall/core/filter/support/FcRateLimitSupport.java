package priv.szf.fastcall.core.filter.support;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.model.FcRateLimitPak;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.config.FastCallProperties;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FcRateLimitSupport {

    private final Cache<Long, List<FcRateLimitPak>> cache = new TimedCache<>(0);

    private final Map<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private final IFcPakProvider pakProvider;

    private final FastCallProperties properties;

    public void init() {
        Map<Long, List<FcRateLimitPak>> systemRateLimitMap = this.pakProvider.getAllRateLimits()
                .stream()
                .filter(l -> l.getMaximum() > 0)
                .map(FcRateLimitPak::<FcRateLimitPak>check)
                .collect(Collectors.groupingBy(FcRateLimitPak::getSystemId));

        this.properties.getEasySource()
                .stream()
                .filter(s -> CollectionUtil.isNotEmpty(s.getRateLimits()))
                .forEach(s -> {
                    String code = s.getSystem().getCode();
                    Long systemId = FcUtils.encodeId(code);
                    systemRateLimitMap.computeIfAbsent(systemId, k ->
                            s.getRateLimits().stream()
                            .map(r -> {
                                FcRateLimitPak pak = new FcRateLimitPak();
                                pak.setSystemId(systemId);
                                pak.setSpan(r.getSpan());
                                pak.setMaximum(r.getMaximum());
                                return pak.<FcRateLimitPak>check();
                            })
                            .collect(Collectors.toList()));
                });

        systemRateLimitMap.forEach(this.cache::put);
    }

    public List<FcRateLimitPak> getLimits(Long systemId) {
        return this.cache.get(systemId, false, Collections::emptyList);
    }

    @Async(FastCallConsts.ASYNC_EXECUTOR)
    public void updateLimits(Long systemId, LocalDateTime currentTime) {
        List<FcRateLimitPak> rateLimits = getLimits(systemId);
        if (CollectionUtil.isEmpty(rateLimits)) {
            return;
        }

        ReentrantLock lock = this.lockMap.computeIfAbsent(systemId, key -> new ReentrantLock(true));
        try {
            lock.lock();
            for (FcRateLimitPak rateLimit : rateLimits) {
                LocalDateTime lastTime = rateLimit.getLastTime();
                boolean hasLastTime = Objects.nonNull(lastTime);
                if (hasLastTime) {
                    long current = rateLimit.getCurrent();
                    current++;
                    rateLimit.setCurrent(current);
                } else {
                    rateLimit.setLastTime(currentTime);
                    rateLimit.setCurrent(1);
                }
            }

            this.pakProvider.saveRateLimits(rateLimits);
        } finally {
            lock.unlock();
        }

    }


}
