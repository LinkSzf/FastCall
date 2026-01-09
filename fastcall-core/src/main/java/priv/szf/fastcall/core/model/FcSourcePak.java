package priv.szf.fastcall.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Builder
@Getter
public class FcSourcePak implements IEssentialCheck<FcSourcePak>, Serializable {

    private static final long serialVersionUID = 1169371931895544090L;

    private FcSystemPak system;

    private FcAuthPak auth;

    @Setter
    private ICredential credential;

    private Map<String, FcApiPak> apiMap;

    private List<FcHeaderAssignPak> headerAssigns;

    @Override
    public List<Function<FcSourcePak, ?>> requireNonNull() {
        return Arrays.asList(
                FcSourcePak::getSystem,
                FcSourcePak::getAuth
        );
    }
}
