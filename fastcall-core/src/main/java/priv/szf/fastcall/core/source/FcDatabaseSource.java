package priv.szf.fastcall.core.source;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.common.FcFuncScope;
import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcHeaderAssignPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.credential.ICredential;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcDatabaseSource;
import priv.szf.fastcall.common.source.IFcPakProvider;
import priv.szf.fastcall.common.source.IFcSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Order(300)
@Transactional
@RequiredArgsConstructor
public class FcDatabaseSource extends FcBaseChainSource implements IFcDatabaseSource, IFcChainSource, IFcSource {

    private final IFcPakProvider pakProvider;

    @Override
    protected FcSourcePak tryGetSourcePak(String systemCode) {
        FcSystemPak system = pakProvider.getSystemByCode(systemCode);
        if (Objects.isNull(system)) {
            return null;
        }

        Set<FcFuncScope> scopes = Optional.ofNullable(system.getScope())
                .orElse(Collections.emptySet());

        Long systemId = system.getId();
        FcAuthPak auth = (scopes.contains(FcFuncScope.AUTH)) ?
                pakProvider.getAuthBySysId(systemId) : null;

        Map<String, FcApiPak> apis = (scopes.contains(FcFuncScope.API)) ?
                Collections.unmodifiableMap(pakProvider.getApisBySysId(systemId)) : Collections.emptyMap();

        List<FcHeaderAssignPak> headerAssigns = (scopes.contains(FcFuncScope.HEADER_ASSIGN)) ?
                Collections.unmodifiableList(pakProvider.getHeaderAssignsBySysId(systemId)) : Collections.emptyList();

        return FcSourcePak.builder()
                .system(system)
                .auth(auth)
                .apiMap(apis)
                .headerAssigns(headerAssigns)
                .build();
    }

    @Override
    public void tryUpdateCredential(String system, ICredential credential) {
    }

}
