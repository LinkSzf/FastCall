package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import priv.szf.fastcall.common.FcHeaderType;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Builder
@Getter
public class FcSourcePak implements IFcPak {

    private static final long serialVersionUID = 1169371931895544090L;

    private final FcSystemPak system;

    private final FcAuthPak auth;

    @Setter
    private volatile ICredential credential;

    private final Map<String, FcApiPak> apiMap;

    private final List<FcHeaderAssignPak> headerAssigns;


    public List<FcHeaderAssignPak> getHeaderAssigns(FcHeaderType type) {
        if (headerAssigns == null) {
            return Collections.emptyList();
        }
        return headerAssigns.stream()
                .filter(f -> f.getType() == type || f.getType() == FcHeaderType.REQUEST_RESPONSE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Collections.singletonList(
                this::getSystem
        );
    }
}
