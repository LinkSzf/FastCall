package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import priv.szf.fastcall.data.entity.FcApiParam;
import priv.szf.fastcall.data.mapper.FcApiParamDao;

import java.util.List;

@Primary
@Repository
interface FcApiParamRepository extends FcBaseRepository<FcApiParam>, FcApiParamDao {

    @Override
    default List<FcApiParam> listByApiId(Long apiId) {
        return findAllByApiId(apiId);
    }

    List<FcApiParam> findAllByApiId(Long apiId);

    @Override
    default List<FcApiParam> listByApiIds(List<Long> apiIds) {
        return findAllByApiIdIn(apiIds);
    }

    List<FcApiParam> findAllByApiIdIn(List<Long> apiIds);

    @Override
    default void removeBatchByApiIds(List<Long> apiIds) {
        deleteAllByApiIdIn(apiIds);
    }

    void deleteAllByApiIdIn(List<Long> apiIds);
}
