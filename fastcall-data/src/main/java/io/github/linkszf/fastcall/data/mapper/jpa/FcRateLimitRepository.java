package io.github.linkszf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import io.github.linkszf.fastcall.data.entity.FcRateLimit;
import io.github.linkszf.fastcall.data.mapper.FcRateLimitDao;

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
