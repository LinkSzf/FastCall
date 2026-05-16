package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FastCall {

    private final FastCallClientFactory clientFactory;

    public FastCallClient getClient(String system) {
        return clientFactory.getClient(system);
    }

    public FastCallClient getExistedClient(String system) {
        return FastCallClientFactory.getExistedClient(system);
    }

}
