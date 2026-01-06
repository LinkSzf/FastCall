package priv.szf.fastcall.data.mapper;

import priv.szf.fastcall.data.entity.FcAuth;

public interface FcAuthDao extends FastCallDao<FcAuth> {

    FcAuth getBySystemId(Long systemId);

}
