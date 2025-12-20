package priv.szf.fastcall.core.call.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.FcTokenPak;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@ConditionalOnProperty(
        prefix = "fast-call",
        name = "enable-memory-cache",
        havingValue = "true",
        matchIfMissing = true
)
@Component
@RequiredArgsConstructor
public class FcMemorySource implements IFcSource {

    private final Map<String, FcSourcePak> cache = new ConcurrentHashMap<>();

    private final FcDatabaseSource databaseSource;

    @Override
    public FcSourcePak getSourcePak(String systemCode) {
        return cache.computeIfAbsent(systemCode, databaseSource::getSourcePak);
    }

    @Override
    public <T> FcTokenPak<T> getAccessToken(String systemCode) {
        return (FcTokenPak<T>) getSourcePak(systemCode).getAccessToken();
    }

    @Override
    public <T> void updateAccessToken(String systemCode, FcTokenPak<T> token) {
        FcSourcePak fcSourcePak = getSourcePak(systemCode);
        fcSourcePak.setAccessToken(token);
        if (token.getToken() instanceof String) {
            databaseSource.updateAccessToken(systemCode, token);
        }
    }

}
