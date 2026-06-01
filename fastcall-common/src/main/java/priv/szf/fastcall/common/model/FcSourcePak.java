package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import priv.szf.fastcall.common.FcHeaderType;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@Getter
public class FcSourcePak implements IEssentialCheck<FcSourcePak>, Serializable {

    private static final long serialVersionUID = 1169371931895544090L;

    private FcSystemPak system;

    private FcAuthPak auth;

    @Setter
    private volatile ICredential credential;

    private Map<String, FcApiPak> apiMap;

    private List<FcHeaderAssignPak> headerAssigns;


    public List<FcHeaderAssignPak> getHeaderAssigns(FcHeaderType type) {
        if (headerAssigns == null) {
            return Collections.emptyList();
        }
        return headerAssigns.stream()
                .filter(f -> f.getType() == type || f.getType() == FcHeaderType.REQUEST_RESPONSE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Function<FcSourcePak, ?>> requireNonNull() {
        return Collections.singletonList(
                FcSourcePak::getSystem
        );
    }
}
