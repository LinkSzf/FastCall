package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Objects;

@Primary
@Repository
interface FcSystemRepository extends FcBaseRepository<FcSystem>, FcSystemDao {

    @Override
    default boolean existSameCode(Long id, String code) {
        FcSystem system = findByCode(code);
        return Objects.nonNull(system) && !Objects.equals(system.getId(), id);
    }

    @Override
    default FcSystem getByCode(String code) {
        return findByCode(code);
    }

    FcSystem findByCode(String code);
}
