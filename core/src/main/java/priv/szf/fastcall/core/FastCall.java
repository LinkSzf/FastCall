package priv.szf.fastcall.core;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.FastCallClient;
import priv.szf.fastcall.core.call.FastCallClientFactory;

@Component
@RequiredArgsConstructor
public class FastCall {

    private final FastCallClientFactory clientFactory;

    public FastCallClient getClient(String systemCode) {
        return clientFactory.getClient(systemCode);
    }

    public Builder to(String systemCode) {
        return new Builder(systemCode);
    }

    @AllArgsConstructor
    public class Builder {
        private final String systemCode;
    }

}
