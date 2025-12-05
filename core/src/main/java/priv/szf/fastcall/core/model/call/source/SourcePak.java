package priv.szf.fastcall.core.model.call.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcSystemPak;

@Data
@AllArgsConstructor
public class SourcePak {

    private FcSystemPak system;

    private FcAuthPak auth;

    private FcApiPak api;

}
