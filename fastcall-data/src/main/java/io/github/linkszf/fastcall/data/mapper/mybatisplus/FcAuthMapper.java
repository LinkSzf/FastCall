package io.github.linkszf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import io.github.linkszf.fastcall.data.entity.FcAuth;
import io.github.linkszf.fastcall.data.mapper.FcAuthDao;

import java.util.Collection;
import java.util.List;

@Mapper
interface FcAuthMapper extends FcBaseMapper<FcAuth>, FcAuthDao {

    @Override
    default List<FcAuth> listBySystemIds(Collection<Long> systemIds) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .in(FcAuth::getSysId, systemIds);
        return selectList(qw);
    }

    @Override
    default FcAuth getBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        return selectOne(qw);
    }

    @Override
    default void removeBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        delete(qw);
    }



}
