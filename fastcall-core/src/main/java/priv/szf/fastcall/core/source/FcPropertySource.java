package priv.szf.fastcall.core.source;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcClientSettingPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FcPropertySource extends FcBaseChainSource implements IFcSource {

    private final FastCallProperties properties;

    private final Map<String, FcSourcePak> pakMap = new HashMap<>();

    @Override
    public void init() {
        properties.getEasySource().stream()
                .map(es -> FcSourcePak.builder()
                        .system(getFcSystemPak(es))
                        .auth(getFcAuthPak(es))
                        .build()
                )
                .forEach(pak -> pakMap.put(pak.getSystem().getCode(), pak));
    }

    @Override
    protected FcSourcePak tryGetSourcePak(String systemCode) {
        return pakMap.get(systemCode);
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
    }

    @Override
    public int getWeight() {
        return Integer.MIN_VALUE;
    }

    private FcSystemPak getFcSystemPak(FastCallProperties.EasySource es) {
        FastCallProperties.EasySource.System esSystem = es.getSystem();
        FcSystemPak system = new FcSystemPak();
        system.setName(esSystem.getName());
        system.setCode(esSystem.getCode());
        system.setHost(esSystem.getHost());
        system.setEnable(esSystem.isEnable());
        FcClientSettingPak cs = new FcClientSettingPak();
        cs.setConnectTimeout(esSystem.getConnectTimeout());
        cs.setReadTimeout(esSystem.getReadTimeout());
        cs.setWriteTimeout(esSystem.getWriteTimeout());
        system.setClientSetting(cs);
        return system;
    }

    private FcAuthPak getFcAuthPak(FastCallProperties.EasySource es) {
        FastCallProperties.EasySource.Auth esAuth = es.getAuth();
        FcAuthPak auth = new FcAuthPak();
        auth.setType(esAuth.getType());
        String authContentStr = esAuth.getContent();
        BaseAuthContent authContent = JSONUtil.toBean(authContentStr, esAuth.getType().getClazz());
        authContent.setType(esAuth.getType());
        auth.setContent(authContent);
        auth.setPath(esAuth.getPath());
        auth.setParticularHost(esAuth.getParticularHost());
        return auth;
    }
}



