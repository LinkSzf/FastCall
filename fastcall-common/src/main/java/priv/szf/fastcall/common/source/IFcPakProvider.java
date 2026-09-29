package priv.szf.fastcall.common.source;

import priv.szf.fastcall.common.model.FcApiPak;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcHeaderAssignPak;
import priv.szf.fastcall.common.model.FcRateLimitPak;
import priv.szf.fastcall.common.model.FcRetryPak;
import priv.szf.fastcall.common.model.FcSystemPak;

import java.util.List;
import java.util.Map;

/**
 * Source of the configured paks of a system, covering system, auth, apis, header assigns and retry.
 * Implemented by the database-backed provider and consumed by {@code FcDatabaseSource}.
 */
public interface IFcPakProvider {

    FcSystemPak getSystemByCode(String systemCode);

    FcAuthPak getAuthBySysId(Long sysId);

    Map<String, FcApiPak> getApisBySysId(Long sysId);

    List<FcHeaderAssignPak> getHeaderAssignsBySysId(Long systemId);

    List<FcRateLimitPak> getAllRateLimits();

    void saveRateLimits(Long sysId, List<FcRateLimitPak> rateLimitPaks);

    FcRetryPak getRetryBySysId(Long sysId);

}
