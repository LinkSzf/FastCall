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
        FcSystemPak system = getSystemByCode(systemCode);
        if (Objects.isNull(system)) {
            return null;
        }

        Long systemId = system.getId();
        Set<FcFuncScope> scopes = getScopes(system);
        FcAuthPak auth = getAuth(scopes, systemId);
        Map<String, FcApiPak> apis = getApis(scopes, systemId);
        List<FcHeaderAssignPak> headerAssigns = getHeaderAssigns(scopes, systemId);

        return FcSourcePak.builder()
                .system(system)
                .auth(auth)
                .apiMap(apis)
                .headerAssigns(headerAssigns)
                .build()
                .init();
    }

    @Override
    public void tryUpdateCredential(String system, ICredential credential) {
    }

    private List<FcHeaderAssignPak> getHeaderAssigns(Set<FcFuncScope> scopes, Long systemId) {
        return (scopes.contains(FcFuncScope.HEADER_ASSIGN)) ?
                Collections.unmodifiableList(this.pakProvider.getHeaderAssignsBySysId(systemId)) : Collections.emptyList();
    }

    private Map<String, FcApiPak> getApis(Set<FcFuncScope> scopes, Long systemId) {
        return (scopes.contains(FcFuncScope.API)) ?
                Collections.unmodifiableMap(this.pakProvider.getApisBySysId(systemId)) : Collections.emptyMap();
    }

    private FcAuthPak getAuth(Set<FcFuncScope> scopes, Long systemId) {
        return (scopes.contains(FcFuncScope.AUTH)) ?
                this.pakProvider.getAuthBySysId(systemId) : null;
    }

    private Set<FcFuncScope> getScopes(FcSystemPak system) {
        return Optional.ofNullable(system.getScope())
                .orElse(Collections.emptySet());
    }

    private FcSystemPak getSystemByCode(String systemCode) {
        return this.pakProvider.getSystemByCode(systemCode);
    }

}
