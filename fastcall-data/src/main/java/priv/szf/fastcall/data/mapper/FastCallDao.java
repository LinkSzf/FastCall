package priv.szf.fastcall.data.mapper;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

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
}
