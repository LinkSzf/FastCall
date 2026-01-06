package priv.szf.fastcall.data.mapper.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
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
        return getReferenceById(id);
    }

    @Override
    default T insertOrUpdate(T entity) {
        return save(entity);
    }

    @Override
    default List<T> insertOrUpdateBatch(List<T> entities) {
        return saveAll(entities);
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
