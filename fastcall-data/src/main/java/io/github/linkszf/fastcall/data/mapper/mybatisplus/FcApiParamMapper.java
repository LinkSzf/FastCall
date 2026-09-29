package io.github.linkszf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import io.github.linkszf.fastcall.data.entity.FcApiParam;
import io.github.linkszf.fastcall.data.mapper.FcApiParamDao;

import java.util.List;

@Mapper
interface FcApiParamMapper extends FcBaseMapper<FcApiParam>, FcApiParamDao {

    @Override
    default List<FcApiParam> listByApiId(Long apiId) {
        LambdaQueryWrapper<FcApiParam> qw = Wrappers.<FcApiParam>lambdaQuery()
                .eq(FcApiParam::getApiId, apiId);
        return selectList(qw);
    }

    @Override
    default List<FcApiParam> listByApiIds(List<Long> apiIds) {
        LambdaQueryWrapper<FcApiParam> qw = Wrappers.<FcApiParam>lambdaQuery().in(FcApiParam::getApiId, apiIds);
        return selectList(qw);
    }

    @Override
    default void removeBatchByApiIds(List<Long> apiIds) {
        LambdaQueryWrapper<FcApiParam> qw = Wrappers.<FcApiParam>lambdaQuery()
                .in(FcApiParam::getApiId, apiIds);
        delete(qw);
    }
}
