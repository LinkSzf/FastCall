package priv.szf.fastcall.core.call.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;

import java.util.List;

@Data
@AllArgsConstructor
public class SourcePak {

    private FcSystem system;

    private FcAuth auth;

    private List<FcApi> apis;


}
