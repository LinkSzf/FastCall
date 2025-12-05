package priv.szf.fastcall.core.model.call.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.call.FcUtils;

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

    private final Map<String, SourcePak> cache = new ConcurrentHashMap<>();

    private final FcDatabaseSource databaseSource;


    @Override
    public SourcePak getSourcePak(String systemCode, String apiName) {
        String key = FcUtils.getApiUniqueCode(systemCode, apiName);
        return cache.computeIfAbsent(key, k -> databaseSource.getSourcePak(systemCode, apiName));
    }
}
