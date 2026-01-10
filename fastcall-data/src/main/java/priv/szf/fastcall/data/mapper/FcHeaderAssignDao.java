package priv.szf.fastcall.data.mapper;

import priv.szf.fastcall.data.entity.FcHeaderAssign;

import java.util.List;

public interface FcHeaderAssignDao extends FastCallDao<FcHeaderAssign> {

    List<FcHeaderAssign> listBySystemId(Long systemId);

}
