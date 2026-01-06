package priv.szf.fastcall.data.mapper.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Objects;


@Mapper
interface FcSystemMapper extends FcBaseMapper<FcSystem>, FcSystemDao {

    @Override
    default boolean existSameCode(Long id, String code) {
        FcSystem system = getByCode(code);
        return Objects.nonNull(system) && !Objects.equals(system.getId(), id);
    }

    @Override
    default FcSystem getByCode(String code) {
        LambdaQueryWrapper<FcSystem> systemQw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, code);
        return selectOne(systemQw);
    }
}
