package io.github.linkszf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import io.github.linkszf.fastcall.data.entity.FcApi;
import io.github.linkszf.fastcall.data.mapper.FcApiDao;

import java.util.List;

@Mapper
interface FcApiMapper extends FcBaseMapper<FcApi>, FcApiDao {

    @Override
    default List<FcApi> listBySystemId(Long systemId) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, systemId);
        return selectList(qw);
    }

    @Override
    default FcApi getOneByNameAndSysId(Long sysId, String apiName) {
        LambdaQueryWrapper<FcApi> qw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, sysId)
                .eq(FcApi::getName, apiName);
        return selectOne(qw);
    }
}
