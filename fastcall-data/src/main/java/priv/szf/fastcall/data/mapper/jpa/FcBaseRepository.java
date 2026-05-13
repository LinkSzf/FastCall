package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import priv.szf.fastcall.data.mapper.FastCallDao;

import java.util.List;

@NoRepositoryBean
public interface FcBaseRepository<T> extends JpaRepository<T, Long>, FastCallDao<T> {

    @Override
    default List<T> list() {
        return findAll();
    }

    @Override
    default T getOneById(Long id) {
        return findById(id).orElse(null);
    }

    @Transactional
    @Override
    default void updateOneById(Long id, T entity) {
        boolean exists = existsById(id);
        if (exists) {
            save(entity);
        }
    }

    @Override
    default T saveOne(T entity) {
        return saveAndFlush(entity);
    }

    @Override
    default List<T> saveBatch(List<T> entities) {
        return saveAllAndFlush(entities);
    }

    @Override
    default void removeById(Long id) {
        deleteById(id);
    }

    @Override
    default void removeBatchByIds(List<Long> ids) {
        deleteAllById(ids);
    }

    @Override
    default void removeBatchNotInIds(List<Long> ids) {
        deleteAllByIdNotIn(ids);
    }

    void deleteAllByIdNotIn(List<Long> ids);


}
