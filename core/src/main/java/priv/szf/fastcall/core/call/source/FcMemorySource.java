package priv.szf.fastcall.core.call.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(
        prefix = "fast-call",
        name = "enable-memory-cache",
        havingValue = "true",
        matchIfMissing = true
)
@Component
@RequiredArgsConstructor
public class FcMemorySource extends FcBaseSource implements IFcSource {

    private final Map<String, FcSourcePak> cache = new ConcurrentHashMap<>();

    @Override
    public FcSourcePak getSourcePak(String systemCode) {
        return cache.computeIfAbsent(systemCode, getNextSource()::getSourcePak);
    }

    @Override
    public <T> void updateAccessToken(String systemCode, FcTokenPak<T> token) {
        FcSourcePak fcSourcePak = getSourcePak(systemCode);
        fcSourcePak.setAccessToken(token);

        getNextSource().updateAccessToken(systemCode, token);
    }

    @Override
    public int getWeight() {
        return 2;
    }

}
