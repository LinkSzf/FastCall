package priv.szf.fastcall.core.call.auth.provider.digest;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientNonceMagnager {

    private static final Map<String, ClientNonceMagnager> MAGNAGER_MAP = new ConcurrentHashMap<>();

    private final Map<String, AtomicInteger> nonceCounters = new ConcurrentHashMap<>();

    public static ClientNonceMagnager system(String system) {
        return MAGNAGER_MAP.computeIfAbsent(system, k -> new ClientNonceMagnager());
    }

    public synchronized int getNextNc(String nonce) {
        AtomicInteger counter = nonceCounters.computeIfAbsent(nonce, k -> new AtomicInteger(1));

        return counter.getAndIncrement();
    }

    public int getCurrentNc(String nonce) {
        return nonceCounters.computeIfAbsent(nonce, k -> new AtomicInteger(1))
                .get();
    }

}
