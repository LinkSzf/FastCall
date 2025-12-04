package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class Calls {

    private final FastCallClientFactory clientFactory;

    private static final Map<String, FastCallClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public FastCallClient newCall(String systemCode) {
        return CLIENT_MAP.computeIfAbsent(systemCode, clientFactory::createNewCallClient);
    }


}
