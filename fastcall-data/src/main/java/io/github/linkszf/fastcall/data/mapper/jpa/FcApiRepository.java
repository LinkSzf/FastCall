package io.github.linkszf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import io.github.linkszf.fastcall.data.entity.FcApi;
import io.github.linkszf.fastcall.data.mapper.FcApiDao;

import java.util.List;

@Primary
@Repository
interface FcApiRepository extends FcBaseRepository<FcApi>, FcApiDao {

    @Override
    default List<FcApi> listBySystemId(Long systemId) {
        return findAllBySysId(systemId);
    }

    List<FcApi> findAllBySysId(Long systemId);

    @Override
    default FcApi getOneByNameAndSysId(Long sysId, String apiName) {
        return findBySysIdAndName(sysId, apiName);
    }

    FcApi findBySysIdAndName(Long sysId, String apiName);
}
