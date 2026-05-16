package priv.szf.fastcall.data.mapper;

import priv.szf.fastcall.data.entity.FcAuth;

import java.util.Collection;
import java.util.List;

public interface FcAuthDao extends FastCallDao<FcAuth> {

    List<FcAuth> listBySystemIds(Collection<Long> systemIds);

    FcAuth getBySystemId(Long systemId);

    void removeBySystemId(Long systemId);
}
