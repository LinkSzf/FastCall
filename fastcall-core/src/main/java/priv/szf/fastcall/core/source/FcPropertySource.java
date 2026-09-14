package priv.szf.fastcall.core.source;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.source.IFcChainSource;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.FcUtils;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.common.model.FcAuthPak;
import priv.szf.fastcall.common.model.FcClientSettingPak;
import priv.szf.fastcall.common.model.FcRetryPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.content.BaseAuthContent;
import priv.szf.fastcall.common.model.credential.ICredential;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Order(200)
@RequiredArgsConstructor
public class FcPropertySource extends FcBaseChainSource implements IFcChainSource, IFcSource {

    private final List<FastCallProperties.EasySource> sources;

    private final Map<String, FcSourcePak> pakMap = new HashMap<>();

    @Override
    public void init() {
        this.sources.stream()
                .map(this::buildToPak)
                .forEach(pak -> {
                    String code = pak.getSystem().getCode();
                    if (this.pakMap.containsKey(code)) {
                        throw new FcUnexpectedException("Duplicated system code in fast-call.easy-source: {}", code);
                    }
                    this.pakMap.put(code, pak);
                });
    }

    @Override
    protected FcSourcePak tryGetSourcePak(String system) {
        return this.pakMap.get(system);
    }

    @Override
    protected void tryUpdateCredential(String system, ICredential credential) {
    }

    private FcSourcePak buildToPak(FastCallProperties.EasySource es) {
        FcSourcePak sourcePak = FcSourcePak.builder()
                .system(getFcSystemPak(es))
                .auth(getFcAuthPak(es))
                .retry(getFcRetryPak(es.getRetry()))
                .build();
        sourcePak.init();
        return sourcePak;
    }

    private FcRetryPak getFcRetryPak(FastCallProperties.EasySource.Retry esRetry) {
        if (Objects.isNull(esRetry)) {
            return null;
        }

        return FcRetryPak.builder()
                .attempts(esRetry.getAttempts())
                .duration(esRetry.getDuration())
                .build();
    }

    private FcSystemPak getFcSystemPak(FastCallProperties.EasySource es) {
        FastCallProperties.EasySource.System esSystem = es.getSystem();
        FcClientSettingPak cs = FcClientSettingPak.builder()
                .connectTimeout(esSystem.getConnectTimeout())
                .readTimeout(esSystem.getReadTimeout())
                .writeTimeout(esSystem.getWriteTimeout())
                .build();

        return FcSystemPak.builder()
                .clientSetting(cs)
                .id(FcUtils.encodeId(esSystem.getCode()))
                .name(esSystem.getName())
                .code(esSystem.getCode())
                .host(esSystem.getHost())
                .enable(esSystem.isEnable())
                .authType(esSystem.getAuthType())
                .build();
    }

    private FcAuthPak getFcAuthPak(FastCallProperties.EasySource es) {
        FastCallProperties.EasySource.System esSystem = es.getSystem();
        FcAuthType authType = esSystem.getAuthType();
        if (authType == FcAuthType.NONE) {
            return null;
        }

        FastCallProperties.EasySource.Auth esAuth = es.getAuth();
        String authContentStr = esAuth.getContent();
        BaseAuthContent authContent = JSONUtil.toBean(authContentStr, authType.getClazz());
        authContent.setType(authType);

        return FcAuthPak.builder()
                .path(esAuth.getPath())
                .particularHost(esAuth.getParticularHost())
                .unauthorizedCode(esAuth.getUnauthorizedCode())
                .content(authContent)
                .build();
    }
}



