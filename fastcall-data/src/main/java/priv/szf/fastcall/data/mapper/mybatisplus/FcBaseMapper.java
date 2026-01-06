package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import priv.szf.fastcall.data.mapper.FastCallDao;

import java.util.List;


public interface FcBaseMapper<T> extends BaseMapper<T>, FastCallDao<T> {

    @Override
    default List<T> list() {
        return selectList(Wrappers.emptyWrapper());
    }

    @Override
    default T getOneById(Long id) {
        return selectById(id);
    }

    @Override
    default T insertOrUpdate(T entity) {
        Db.saveOrUpdate(entity);
        return entity;
    }

    @Override
    default List<T> insertOrUpdateBatch(List<T> entities) {
        Db.saveOrUpdateBatch(entities);
        return entities;
    }

    @Override
    default void removeById(Long id) {
        deleteById(id);
    }

    @Override
    default void removeBatchByIds(List<Long> ids) {
        deleteBatchIds(ids);
    }

    @Override
    default void removeBatchNotInIds(List<Long> ids) {
        QueryWrapper<T> qw = Wrappers.<T>query()
                .notIn("id", ids);
        delete(qw);
    }
}
