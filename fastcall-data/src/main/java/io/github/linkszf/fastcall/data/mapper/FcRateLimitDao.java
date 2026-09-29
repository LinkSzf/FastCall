package io.github.linkszf.fastcall.data.mapper;


import io.github.linkszf.fastcall.data.entity.FcRateLimit;

import java.util.List;

public interface FcRateLimitDao extends FastCallDao<FcRateLimit> {

    List<FcRateLimit> listAllEnable();

    
}
