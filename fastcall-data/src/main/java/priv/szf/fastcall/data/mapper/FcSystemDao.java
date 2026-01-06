package priv.szf.fastcall.data.mapper;

import priv.szf.fastcall.data.entity.FcSystem;

public interface FcSystemDao extends FastCallDao<FcSystem> {

    boolean existSameCode(Long id, String code);

    FcSystem getByCode(String code);
}
