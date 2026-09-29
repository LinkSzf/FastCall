package io.github.linkszf.fastcall.common.source;

import io.github.linkszf.fastcall.common.model.FcApiPak;
import io.github.linkszf.fastcall.common.model.FcAuthPak;
import io.github.linkszf.fastcall.common.model.FcRateLimitPak;
import io.github.linkszf.fastcall.common.model.FcRetryPak;
import io.github.linkszf.fastcall.common.model.FcSystemPak;

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

    List<FcRateLimitPak> getAllRateLimits();

    void saveRateLimits(Long sysId, List<FcRateLimitPak> rateLimitPaks);

    FcRetryPak getRetryBySysId(Long sysId);

}
