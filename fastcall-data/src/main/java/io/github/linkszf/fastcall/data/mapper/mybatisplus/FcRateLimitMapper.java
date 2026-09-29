package io.github.linkszf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import io.github.linkszf.fastcall.data.entity.FcRateLimit;
import io.github.linkszf.fastcall.data.mapper.FcRateLimitDao;

import java.util.List;


@Mapper
interface FcRateLimitMapper extends FcBaseMapper<FcRateLimit>, FcRateLimitDao {

    @Override
    default List<FcRateLimit> listAllEnable() {
        LambdaQueryWrapper<FcRateLimit> queryWrapper = Wrappers.<FcRateLimit>lambdaQuery()
                .eq(FcRateLimit::getEnable, true);
        return selectList(queryWrapper);
    }


}
