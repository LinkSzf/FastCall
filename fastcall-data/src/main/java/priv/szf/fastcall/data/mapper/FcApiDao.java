package priv.szf.fastcall.data.mapper;


import priv.szf.fastcall.data.entity.FcApi;

import java.util.List;

public interface FcApiDao extends FastCallDao<FcApi> {

    List<FcApi> listBySystemId(Long systemId);

}
