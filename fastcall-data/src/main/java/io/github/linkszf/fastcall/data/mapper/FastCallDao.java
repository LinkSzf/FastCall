package io.github.linkszf.fastcall.data.mapper;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

/**
 * Persistence-agnostic data access contract for the fastcall entities, keyed by {@code Long} ids.
 * Implemented by the JPA repository and MyBatis-Plus mapper base interfaces of the data module.
 */
@NoRepositoryBean
public interface FastCallDao<T> {

    List<T> list();

    List<T> listByIds(Collection<Long> ids);

    T getOneById(Long id);

    void updateOneById(Long id, T entity);

    T saveOne(T entity);

    List<T> saveBatch(Collection<T> entities);

    void removeById(Long id);

    void removeBatchByIds(Collection<Long> ids);

    void removeBatchNotInIds(Collection<Long> ids);

    boolean exists(Long id);
}
