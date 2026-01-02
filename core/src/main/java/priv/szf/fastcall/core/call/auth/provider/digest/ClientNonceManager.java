package priv.szf.fastcall.core.call.auth.provider.digest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientNonceManager {

    private static final int INIT_NC = 1;

    private final Map<String, AtomicInteger> nonceCounters = new ConcurrentHashMap<>();

    public synchronized int getNextNc(String nonce) {
        AtomicInteger counter = this.nonceCounters.computeIfAbsent(nonce, k -> new AtomicInteger(INIT_NC));
        return counter.getAndIncrement();
    }

    public boolean exist(String nonce) {
        return this.nonceCounters.containsKey(nonce);
    }

    public void remove(String nonce) {
        this.nonceCounters.remove(nonce);
    }
}
