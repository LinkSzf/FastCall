package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.mapper.FcAuthDao;

import java.util.Collection;
import java.util.List;

@Primary
@Repository
interface FcAuthRepository extends FcBaseRepository<FcAuth>, FcAuthDao {

    @Override
    default List<FcAuth> listBySystemIds(Collection<Long> systemIds) {
        return findAllBySysIdIn(systemIds);
    }

    List<FcAuth> findAllBySysIdIn(Collection<Long> systemIds);

    @Override
    default FcAuth getBySystemId(Long systemId) {
        return findBySysId(systemId);
    }

    FcAuth findBySysId(Long systemId);

    @Override
    default void removeBySystemId(Long systemId) {
        deleteBySysId(systemId);
    }

    void deleteBySysId(Long systemId);

}
