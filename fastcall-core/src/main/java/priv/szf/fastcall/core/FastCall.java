package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FastCall {

    private final FastCallClientFactory clientFactory;

    public FastCallClient getClient(String systemCode) {
        return clientFactory.getClient(systemCode);
    }

}
