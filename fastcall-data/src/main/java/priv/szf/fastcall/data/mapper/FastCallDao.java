package priv.szf.fastcall.data.mapper;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface FastCallDao<T> {

    List<T> list();

    T getOneById(Long id);

    T insertOrUpdate(T entity);

    List<T> insertOrUpdateBatch(List<T> entities);

    void removeById(Long id);

    void removeBatchByIds(List<Long> ids);

    void removeBatchNotInIds(List<Long> ids);
}
