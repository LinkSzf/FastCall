package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import priv.szf.fastcall.data.entity.FcRateLimit;
import priv.szf.fastcall.data.mapper.FcRateLimitDao;

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
