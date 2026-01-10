package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import priv.szf.fastcall.data.entity.FcHeaderAssign;
import priv.szf.fastcall.data.mapper.FcHeaderAssignDao;

import java.util.List;

@Mapper
public interface FcHeaderAssignMapper extends FcBaseMapper<FcHeaderAssign>, FcHeaderAssignDao {

    @Override
    default List<FcHeaderAssign> listBySystemId(Long systemId) {
        LambdaQueryWrapper<FcHeaderAssign> qw = Wrappers.<FcHeaderAssign>lambdaQuery()
                .eq(FcHeaderAssign::getSysId, systemId);
        return selectList(qw);
    }
}
