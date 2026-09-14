package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcDatabaseSource;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Objects;

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
                .headerAssigns(this.pakProvider.getHeaderAssignsBySysId(systemId))
                .retry(this.pakProvider.getRetryBySysId(systemId))
                .build();

        sourcePak.init();

        return sourcePak;
    }

    @Override
    public void tryUpdateCredential(String system, ICredential credential) {
    }

}
