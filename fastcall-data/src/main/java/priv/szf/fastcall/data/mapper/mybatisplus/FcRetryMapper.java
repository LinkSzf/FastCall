package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import priv.szf.fastcall.data.entity.FcRetry;
import priv.szf.fastcall.data.mapper.FcRetryDao;

import java.util.Collection;
import java.util.List;


@Mapper
interface FcRetryMapper extends FcBaseMapper<FcRetry>, FcRetryDao {

    @Override
    default List<FcRetry> listBySystemIds(Collection<Long> systemIds) {
        LambdaQueryWrapper<FcRetry> qw = Wrappers.<FcRetry>lambdaQuery()
                .in(FcRetry::getSysId, systemIds);
        return selectList(qw);
    }

    @Override
    default FcRetry getBySystemId(Long systemId) {
        LambdaQueryWrapper<FcRetry> qw = Wrappers.<FcRetry>lambdaQuery()
                .eq(FcRetry::getSysId, systemId);
        return selectOne(qw);
    }

    @Override
    default void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcRetry> qw = Wrappers.<FcRetry>lambdaQuery()
                .eq(FcRetry::getSysId, systemId);
        delete(qw);
    }

}
