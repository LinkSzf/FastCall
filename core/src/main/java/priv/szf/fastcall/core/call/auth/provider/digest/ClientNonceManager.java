package priv.szf.fastcall.core.call.auth.provider.digest;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientNonceManager {

    private static final int INIT_NC = 1;

    private static final Map<String, ClientNonceManager> MAGNAGER_MAP = new ConcurrentHashMap<>();

    private static final ThreadLocal<String> CURRENT_NONCE = new ThreadLocal<>();

    private final Map<String, AtomicInteger> nonceCounters = new ConcurrentHashMap<>();

    public static ClientNonceManager system(String system) {
        return MAGNAGER_MAP.computeIfAbsent(system, k -> new ClientNonceManager());
    }

    public synchronized int getNextNc(String nonce) {
        AtomicInteger counter = this.nonceCounters.computeIfAbsent(nonce, k -> new AtomicInteger(INIT_NC));
        CURRENT_NONCE.set(nonce);
        return counter.getAndIncrement();
    }

    public int getCurrentNc(String nonce) {
        return this.nonceCounters.computeIfAbsent(nonce, k -> new AtomicInteger(INIT_NC))
                .get();
    }

    public boolean exist(String nonce) {
        return this.nonceCounters.containsKey(nonce);
    }

    public void invalidNonce() {
        String nonce = CURRENT_NONCE.get();
        if (Objects.nonNull(nonce)) {
            this.nonceCounters.remove(nonce);
            CURRENT_NONCE.remove();
        }
    }

}
