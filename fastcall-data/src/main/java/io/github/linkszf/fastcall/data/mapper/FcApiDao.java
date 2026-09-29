package io.github.linkszf.fastcall.data.mapper;


import io.github.linkszf.fastcall.data.entity.FcApi;

import java.util.List;

public interface FcApiDao extends FastCallDao<FcApi> {

    List<FcApi> listBySystemId(Long systemId);

    FcApi getOneByNameAndSysId(Long sysId, String apiName);
}
