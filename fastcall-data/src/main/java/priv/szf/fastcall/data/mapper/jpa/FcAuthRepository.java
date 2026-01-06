package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.mapper.FcAuthDao;

@Primary
@Repository
interface FcAuthRepository extends FcBaseRepository<FcAuth>, FcAuthDao {


    @Override
    default FcAuth getBySystemId(Long systemId) {
        return findBySysId(systemId);
    }

    FcAuth findBySysId(Long systemId);
}
