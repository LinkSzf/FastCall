package priv.szf.fastcall.core.source;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.IEssentialCheck;
import priv.szf.fastcall.core.model.credential.ICredential;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Builder
@Getter
public class FcSourcePak implements IEssentialCheck<FcSourcePak> {

    private FcSystemPak system;

    private FcAuthPak auth;

    @Setter
    private ICredential credential;

    private Map<String, FcApiPak> apiMap;

    @Override
    public List<Function<FcSourcePak, ?>> checkThese() {
        return Arrays.asList(
                FcSourcePak::getSystem,
                FcSourcePak::getAuth
        );
    }
}
