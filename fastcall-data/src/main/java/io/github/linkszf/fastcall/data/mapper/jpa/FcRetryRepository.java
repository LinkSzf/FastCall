package io.github.linkszf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import io.github.linkszf.fastcall.data.entity.FcRetry;
import io.github.linkszf.fastcall.data.mapper.FcRetryDao;

import java.util.Collection;
import java.util.List;

@Primary
@Repository
public interface FcRetryRepository extends FcBaseRepository<FcRetry>, FcRetryDao {

    @Override
    default List<FcRetry> listBySystemIds(Collection<Long> systemIds) {
        return findAllBySysIdIn(systemIds);
    }

    List<FcRetry> findAllBySysIdIn(Collection<Long> systemIds);

    @Override
    default FcRetry getBySystemId(Long systemId) {
        return findBySysId(systemId);
    }

    FcRetry findBySysId(Long systemId);

    @Override
    default void removeBySystemId(Long systemId) {
        deleteBySysId(systemId);
    }

    void deleteBySysId(Long systemId);

}
