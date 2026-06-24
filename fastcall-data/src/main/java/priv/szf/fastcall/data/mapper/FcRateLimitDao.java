package priv.szf.fastcall.data.mapper;


import priv.szf.fastcall.data.entity.FcRateLimit;

import java.util.List;

public interface FcRateLimitDao extends FastCallDao<FcRateLimit> {

    List<FcRateLimit> listAllEnable();

    
}
