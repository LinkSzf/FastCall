package priv.szf.fastcall.core.call;


import lombok.RequiredArgsConstructor;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.auth.interceptor.FcAuthInterceptor;
import priv.szf.fastcall.core.call.auth.interceptor.FcTokenRefreshInterceptor;
import priv.szf.fastcall.core.common.AuthType;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.common.FcUnexpectedException;
import priv.szf.fastcall.core.model.FcClientSettingPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.mapping.FcPakMapping;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class FastCallClientFactory {

    private static final Map<String, FastCallClient> CLIENT_MAP = new ConcurrentHashMap<>();

    private final IFcSource source;

    private final FastCallProperties properties;

    private final ConnectionPool connectionPool;

    private final Dispatcher dispatcher;

    private final FcAuthInterceptor authInceptor;

    private final FcTokenRefreshInterceptor tokenRefreshInterceptor;

    private final FcPakMapping fcPakMapping;

    public static FastCallClient getExistedClient(String systemCode) {
        FastCallClient client = CLIENT_MAP.get(systemCode);
        if (Objects.isNull(client)) {
            throw new FcUnexpectedException(String.format("FastCall-系统[%s]未注册，无法发起请求", systemCode));
        }
        return client;
    }

    public FastCallClient getClient(String systemCode) {
        return CLIENT_MAP.computeIfAbsent(systemCode, this::createNewClient);
    }

    private FastCallClient createNewClient(String systemCode) {
        FcSourcePak sourcePak = source.getSourcePak(systemCode);

        FcSystemPak system = sourcePak.getSystem();
        if (!system.isEnable()) {
            throw new FcBizException(String.format("系统[%s]未配置启用，无法发起访问", systemCode));
        }

        AuthType authType = sourcePak.getAuth().getType();

        FcClientSettingPak clientSetting = getClientSetting(system);
        OkHttpClient okHttpClient = initCoreClient(clientSetting);
        return new FastCallClient(okHttpClient, systemCode, authType, source);
    }

    private FcClientSettingPak getClientSetting(FcSystemPak system) {
        FcClientSettingPak systemSetting = system.getClientSetting();
        FcClientSettingPak globalSetting = fcPakMapping.toClientSettingPak(properties.getClient());
        systemSetting.completeWith(globalSetting);
        return systemSetting;
    }

    private OkHttpClient initCoreClient(FcClientSettingPak settingPak) {
        int connectTimeout = settingPak.getConnectTimeout();
        int readTimeout = settingPak.getReadTimeout();
        int writeTimeout = settingPak.getWriteTimeout();

        Cache cache = Optional.ofNullable(properties.getCache())
                .filter(FastCallProperties.Cache::isEnable)
                .map(cacheSetting -> {
                    File cacheFile = new File(cacheSetting.getPath());
                    return new Cache(cacheFile, cacheSetting.getMaxSize());
                })
                .orElse(null);

        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .addInterceptor(authInceptor)
                .addNetworkInterceptor(tokenRefreshInterceptor)
                .cache(cache)
                .build();
    }

}
