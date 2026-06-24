package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import priv.szf.fastcall.data.entity.FcRateLimit;
import priv.szf.fastcall.data.mapper.FcRateLimitDao;

import java.util.List;

@Primary
@Repository
public interface FcRateLimitRepository extends FcBaseRepository<FcRateLimit>, FcRateLimitDao {

    @Override
    default List<FcRateLimit> listAllEnable() {
        return findByEnable(true);
    }

    List<FcRateLimit> findByEnable(Boolean enable);
}
