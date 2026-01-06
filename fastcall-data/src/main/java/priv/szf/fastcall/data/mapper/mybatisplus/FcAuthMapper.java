package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import priv.szf.fastcall.data.entity.FcAuth;
import priv.szf.fastcall.data.mapper.FcAuthDao;

@Mapper
interface FcAuthMapper extends FcBaseMapper<FcAuth>, FcAuthDao {

    @Override
    default FcAuth getBySystemId(Long systemId) {
        LambdaQueryWrapper<FcAuth> qw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, systemId);
        return selectOne(qw);
    }



}
