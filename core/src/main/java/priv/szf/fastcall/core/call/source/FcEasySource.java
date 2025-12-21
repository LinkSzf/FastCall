package priv.szf.fastcall.core.call.source;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.FcAuthPak;
import priv.szf.fastcall.core.model.FcClientSettingPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.model.FcTokenPak;
import priv.szf.fastcall.core.model.auth.BaseAuthContent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FcEasySource implements IFcSource {

    private final FastCallProperties properties;

    private final Map<String, FcSourcePak> source = new HashMap<>();

    @PostConstruct
    public void init() {
        properties.getEasySource().stream()
                .map(es -> new FcSourcePak(
                        getFcSystemPak(es),
                        getFcAuthPak(es),
                        null,
                        null
                ))
                .forEach(pak -> source.put(pak.getSystem().getCode(), pak));
    }

    @Override
    public FcSourcePak getSourcePak(String systemCode) {
        return source.get(systemCode);
    }

    @Override
    public <T> FcTokenPak<T> getAccessToken(String systemCode) {
        return null;
    }

    @Override
    public <T> void updateAccessToken(String systemCode, FcTokenPak<T> token) {
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



