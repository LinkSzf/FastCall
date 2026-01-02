package priv.szf.fastcall.core.call.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.auth.credential.ICredential;

import java.util.Map;

@Data
@AllArgsConstructor
public class FcSourcePak {

    private FcSystemPak system;

    private FcAuthPak auth;

    private ICredential credential;

    private Map<String, FcApiPak> apiMap;

}
