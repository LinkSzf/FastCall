package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.model.call.FastCallClient;
import priv.szf.fastcall.core.model.call.FastCallClientFactory;

@Component
@RequiredArgsConstructor
public class Calls {

//    private static final Map<String, FastCallClient> CLIENT_MAP = new ConcurrentHashMap<>();

    private final FastCallClientFactory clientFactory;

    public FastCallClient newCall(String systemCode, String apiName) {
//        String clientKey = FcUtils.getClientKey(systemCode, apiName);
//        return CLIENT_MAP.computeIfAbsent(clientKey, clientFactory::createNewCallClient);
        return clientFactory.getClient(systemCode, apiName);
    }




}
