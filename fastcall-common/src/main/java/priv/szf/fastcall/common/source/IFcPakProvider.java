package priv.szf.fastcall.common.source;

import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcHeaderAssignPak;
import priv.szf.fastcall.common.model.FcSystemPak;

import java.util.List;
import java.util.Map;

public interface IFcPakProvider {

    FcSystemPak getSystemByCode(String systemCode);

    FcAuthPak getAuthBySysId(Long sysId);

    Map<String, FcApiPak> getApisBySysId(Long sysId);

    List<FcHeaderAssignPak> getHeaderAssignsBySysId(Long systemId);

}
