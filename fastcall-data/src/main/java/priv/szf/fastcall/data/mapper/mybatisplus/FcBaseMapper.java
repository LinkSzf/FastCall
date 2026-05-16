package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import priv.szf.fastcall.data.mapper.FastCallDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public interface FcBaseMapper<T> extends BaseMapper<T>, FastCallDao<T> {

    @Override
    default List<T> list() {
        return selectList(Wrappers.emptyWrapper());
    }

    @Override
    default List<T> listByIds(Collection<Long> ids) {
        return selectList(Wrappers.<T>query().in("id", ids));
    }

    @Override
    default T getOneById(Long id) {
        return selectById(id);
    }

    @Override
    default void updateOneById(Long id, T entity) {
        updateById(entity);
    }

    @Override
    default T saveOne(T entity) {
        Db.saveOrUpdate(entity);
        return entity;
    }

    @Override
    default List<T> saveBatch(Collection<T> entities) {
        Db.saveOrUpdateBatch(entities);
        return new ArrayList<>(entities);
    }

    @Override
    default void removeById(Long id) {
        deleteById(id);
    }

    @Override
    default void removeBatchByIds(Collection<Long> ids) {
        deleteBatchIds(ids);
    }

    @Override
    default void removeBatchNotInIds(Collection<Long> ids) {
        QueryWrapper<T> qw = Wrappers.<T>query()
                .notIn("id", ids);
        delete(qw);
    }

    @Override
    default boolean exists(Long id) {
        Wrapper<T> wrapper = Wrappers.<T>query()
                .eq("id", id);
        return exists(wrapper);
    }
}
