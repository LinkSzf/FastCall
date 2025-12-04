package priv.szf.fastcall.core.call.source;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.mapper.FcApiMapper;
import priv.szf.fastcall.core.mapper.FcAuthMapper;
import priv.szf.fastcall.core.mapper.FcSystemMapper;
import priv.szf.fastcall.core.model.entity.FcApi;
import priv.szf.fastcall.core.model.entity.FcAuth;
import priv.szf.fastcall.core.model.entity.FcSystem;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FcDatabaseSource implements IFcSource {

    private final FcSystemMapper systemMapper;

    private final FcAuthMapper authMapper;

    private final FcApiMapper apiMapper;

    @Override
    public SourcePak getSourcePak(String code) {
        LambdaQueryWrapper<FcSystem> systemQw = Wrappers.<FcSystem>lambdaQuery()
                .eq(FcSystem::getCode, code);
        FcSystem system = systemMapper.selectOne(systemQw);
        if (Objects.isNull(system)) {
            throw new FcBizException("未找到该系统信息");
        }

        Long systemId = system.getId();
        FcAuth auth = getAuthBySysId(systemId);

        List<FcApi> apiList = getApiListBySysId(systemId);

        return new SourcePak(system, auth, apiList);
    }

    private FcAuth getAuthBySysId(Long sysId) {
        LambdaQueryWrapper<FcAuth> authQw = Wrappers.<FcAuth>lambdaQuery()
                .eq(FcAuth::getSysId, sysId);
        FcAuth auth = authMapper.selectOne(authQw);
        if (Objects.isNull(auth)) {
            throw new FcBizException("未找到该系统认证配置信息");
        }
        return auth;
    }

    private List<FcApi> getApiListBySysId(Long sysId) {
        LambdaQueryWrapper<FcApi> apiQw = Wrappers.<FcApi>lambdaQuery()
                .eq(FcApi::getSysId, sysId);
        return apiMapper.selectList(apiQw);
    }



}
