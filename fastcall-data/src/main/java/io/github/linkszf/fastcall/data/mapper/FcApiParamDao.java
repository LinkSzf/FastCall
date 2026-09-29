package io.github.linkszf.fastcall.data.mapper;

import io.github.linkszf.fastcall.data.entity.FcApiParam;

import java.util.List;

public interface FcApiParamDao extends FastCallDao<FcApiParam> {

    List<FcApiParam> listByApiId(Long apiId);

    List<FcApiParam> listByApiIds(List<Long> apiIds);

    void removeBatchByApiIds(List<Long> apiIds);
}
