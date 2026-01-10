package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;

import java.util.List;

@Primary
@Repository
public interface FcHeaderAssignRepository extends FcBaseRepository<FcHeaderAssign>, FcHeaderAssignDao {

    @Override
    default List<FcHeaderAssign> listBySystemId(Long systemId) {
        return findAllBySysId(systemId);
    }

    List<FcHeaderAssign> findAllBySysId(Long systemId);
}
