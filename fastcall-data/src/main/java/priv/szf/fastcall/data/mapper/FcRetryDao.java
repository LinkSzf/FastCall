package priv.szf.fastcall.data.mapper;


import priv.szf.fastcall.data.entity.FcRetry;

import java.util.Collection;
import java.util.List;

public interface FcRetryDao extends FastCallDao<FcRetry> {

    List<FcRetry> listBySystemIds(Collection<Long> systemIds);

    FcRetry getBySystemId(Long systemId);

    void removeBySystemId(Long systemId);

}
