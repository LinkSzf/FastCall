package io.github.linkszf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import io.github.linkszf.fastcall.common.model.FcSourcePak;
import io.github.linkszf.fastcall.common.model.FcSystemPak;
import io.github.linkszf.fastcall.common.model.credential.ICredential;
import io.github.linkszf.fastcall.common.source.IFcChainSource;
import io.github.linkszf.fastcall.common.source.IFcDatabaseSource;
import io.github.linkszf.fastcall.common.source.IFcPakProvider;
import io.github.linkszf.fastcall.common.source.IFcSource;

import java.util.Objects;

/**
 * Final node of the source chain that loads the pak of a system from the database via {@code IFcPakProvider}.
 * Loaded after the cache and property nodes, and credential updates are not persisted back.
 */
@Order(300)
@Transactional
@RequiredArgsConstructor
public class FcDatabaseSource extends FcBaseChainSource implements IFcDatabaseSource, IFcChainSource, IFcSource {

    private final IFcPakProvider pakProvider;

    @Override
    protected FcSourcePak tryGetSourcePak(String systemCode) {
        FcSystemPak system = this.pakProvider.getSystemByCode(systemCode);
        if (Objects.isNull(system)) {
            return null;
        }

        Long systemId = system.getId();
        FcSourcePak sourcePak = FcSourcePak.builder()
                .system(system)
                .auth(this.pakProvider.getAuthBySysId(systemId))
                .apiMap(this.pakProvider.getApisBySysId(systemId))
                .retry(this.pakProvider.getRetryBySysId(systemId))
                .build();

        sourcePak.init();

        return sourcePak;
    }

    @Override
    public void tryUpdateCredential(String system, ICredential credential) {
    }

}
