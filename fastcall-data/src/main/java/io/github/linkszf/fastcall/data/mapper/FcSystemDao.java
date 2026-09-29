package io.github.linkszf.fastcall.data.mapper;

import io.github.linkszf.fastcall.data.entity.FcSystem;

public interface FcSystemDao extends FastCallDao<FcSystem> {

    boolean existSameCode(Long id, String code);

    FcSystem getByCode(String code);
}
