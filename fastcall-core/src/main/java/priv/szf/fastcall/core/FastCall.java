package priv.szf.fastcall.core;

import lombok.RequiredArgsConstructor;


/**
 * Entry point for obtaining a FastCall client by system code, injected where a call has to be made.
 * Clients are created on first use and cached per system code.
 */
@RequiredArgsConstructor
public class FastCall {

    private final FastCallClientFactory clientFactory;

    /**
     * Returns the client of the given system, creating and caching it on first use.
     *
     * @throws FastCallException if the system has no configuration or is configured as disabled.
     */
    public FastCallClient getClient(String system) {
        return this.clientFactory.getClient(system);
    }

    /**
     * Returns the client that was already created for the given system, without creating one.
     *
     * @throws FastCallException if no client of that system has been instantiated yet.
     */
    public FastCallClient getExistedClient(String system) {
        return FastCallClientFactory.getExistedClient(system);
    }

}
