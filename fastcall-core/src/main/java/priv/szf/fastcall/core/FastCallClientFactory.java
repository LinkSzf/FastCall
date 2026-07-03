package priv.szf.fastcall.core;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.exception.FcDataNotFoundException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.auth.interceptor.FcAuthInterceptor;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.model.FcClientSettingPak;
import priv.szf.fastcall.common.model.FcSystemPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.core.auth.interceptor.FcAuthRefreshInterceptor;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.filter.FcFilterManager;
import priv.szf.fastcall.core.source.FcSourceDelegate;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class FastCallClientFactory {

    private static final Map<String, FastCallClient> CLIENT_MAP = new ConcurrentHashMap<>();
    private static final Object CACHE_LOCK = new Object();

    private final FcSourceDelegate source;

    private final FastCallProperties properties;

    private final ConnectionPool connectionPool;

    private final Dispatcher dispatcher;

    private final FcAuthInterceptor authInterceptor;

    private final FcAuthRefreshInterceptor tokenRefreshInterceptor;

    private final FcFilterManager filterManager;

    private volatile Cache sharedCache;

    public static FastCallClient getExistedClient(String system) {
        FastCallClient client = CLIENT_MAP.get(system);
        if (Objects.isNull(client)) {
            throw new FcUnexpectedException("System[{}] has not been instantiated", system);
        }
        return client;
    }

    public FastCallClient getClient(String system) {
        return CLIENT_MAP.computeIfAbsent(system, this::createNewClient);
    }

    private FastCallClient createNewClient(String system) {
        FcSourcePak sourcePak = source.getSourcePak(system);
        if (Objects.isNull(sourcePak)) {
            throw new FcDataNotFoundException("System[{}] does not have config infos", system);
        }

        FcSystemPak systemPak = sourcePak.getSystem();
        if (!systemPak.isEnable()) {
            throw new FastCallException("System[{}] has been set to disable and access cannot be initiated", system);
        }

        FcClientSettingPak clientSetting = getClientSetting(systemPak);
        OkHttpClient client = initCoreClient(clientSetting);

        log.debug("{}-A new client of system[{}] has been created", FastCallConsts.NAME, system);
        return FastCallClient.builder()
                .client(client)
                .system(system)
                .source(source)
                .filterManager(filterManager)
                .build();
    }

    private FcClientSettingPak getClientSetting(FcSystemPak system) {
        FcClientSettingPak systemSetting = system.getClientSetting();
        FastCallProperties.Client globalSetting = properties.getClient();
        Integer connectTimeout = Optional.ofNullable(systemSetting)
                .map(FcClientSettingPak::getConnectTimeout)
                .orElse(globalSetting.getConnectTimeout());
        Integer readTimeout = Optional.ofNullable(systemSetting)
                .map(FcClientSettingPak::getReadTimeout)
                .orElse(globalSetting.getReadTimeout());
        Integer writeTimeout = Optional.ofNullable(systemSetting)
                .map(FcClientSettingPak::getWriteTimeout)
                .orElse(globalSetting.getWriteTimeout());
        return FcClientSettingPak.builder()
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .writeTimeout(writeTimeout)
                .build();
    }

    private OkHttpClient initCoreClient(FcClientSettingPak settingPak) {
        int connectTimeout = settingPak.getConnectTimeout();
        int readTimeout = settingPak.getReadTimeout();
        int writeTimeout = settingPak.getWriteTimeout();

        Cache cache = resolveSharedCache();

        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .addInterceptor(authInterceptor)
                .addNetworkInterceptor(tokenRefreshInterceptor)
                .cache(cache)
                .build();
    }

    private Cache resolveSharedCache() {
        FastCallProperties.Cache cacheSetting = properties.getCache();
        if (Objects.isNull(cacheSetting) || !cacheSetting.isEnable()) {
            return null;
        }

        Cache cache = this.sharedCache;
        if (Objects.nonNull(cache)) {
            return cache;
        }

        synchronized (CACHE_LOCK) {
            if (Objects.isNull(this.sharedCache)) {
                File cacheFile = new File(cacheSetting.getPath());
                this.sharedCache = new Cache(cacheFile, cacheSetting.getMaxSize());
            }
            return this.sharedCache;
        }
    }

    public void removeClient(String system) {
        CLIENT_MAP.remove(system);
    }
}

