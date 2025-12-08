package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;

@Component
@RequiredArgsConstructor
public class Calls {

    private final FastCallClientFactory clientFactory;

    public FastCallClient newCall(String systemCode, String apiName) {
        return clientFactory.getClient(systemCode);
    }






}
