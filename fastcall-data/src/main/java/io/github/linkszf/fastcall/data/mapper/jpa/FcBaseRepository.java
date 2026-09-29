package io.github.linkszf.fastcall.data.mapper.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import io.github.linkszf.fastcall.data.mapper.FastCallDao;

import java.util.Collection;
import java.util.List;

/**
 * JPA base repository that implements {@code FastCallDao} on top of Spring Data's {@code JpaRepository}.
 * Saving uses the flush-immediately variants, and the not-in batch delete is a derived query method.
 */
@NoRepositoryBean
public interface FcBaseRepository<T> extends JpaRepository<T, Long>, FastCallDao<T> {

    @Override
    default List<T> list() {
        return findAll();
    }

    @Override
    default List<T> listByIds(Collection<Long> ids) {
        return findAllById(ids);
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
    default List<T> saveBatch(Collection<T> entities) {
        return saveAllAndFlush(entities);
    }

    @Override
    default void removeById(Long id) {
        deleteById(id);
    }

    @Override
    default void removeBatchByIds(Collection<Long> ids) {
        deleteAllById(ids);
    }

    @Override
    default void removeBatchNotInIds(Collection<Long> ids) {
        deleteAllByIdNotIn(ids);
    }

    void deleteAllByIdNotIn(Collection<Long> ids);

    @Override
    default boolean exists(Long id) {
        return existsById(id);
    }
}
