package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FastCall {

    private final FastCallClientFactory clientFactory;

    public FastCallClient getClient(String system) {
        return this.clientFactory.getClient(system);
    }

    public FastCallClient getExistedClient(String system) {
        return FastCallClientFactory.getExistedClient(system);
    }

}
