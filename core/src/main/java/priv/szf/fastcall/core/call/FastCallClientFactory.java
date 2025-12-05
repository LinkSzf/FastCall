package priv.szf.fastcall.core.call;


import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.Auth.FcAuthInceptor;
import priv.szf.fastcall.core.common.FcBizException;
import priv.szf.fastcall.core.common.FcDataNotFoundException;
import priv.szf.fastcall.core.model.FcApiPak;
import priv.szf.fastcall.core.model.FcClientSettingPak;
import priv.szf.fastcall.core.model.FcSystemPak;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.call.source.FcSourcePak;
import priv.szf.fastcall.core.config.FastCallProperties;

import java.util.Map;
import java.util.Objects;
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

    private final FcAuthInceptor authInceptor;

    public FastCallClient createNewCallClient(FcSourcePak sourcePak) {
        OkHttpClient okHttpClient = initOkHttpClient(sourcePak);
        return new FastCallClient(okHttpClient, sourcePak);
    }

    private OkHttpClient initOkHttpClient(FcSourcePak sourcePak) {
        FcClientSettingPak apiSetting = sourcePak.getApi().getClientSetting();
        FcClientSettingPak systemSetting = sourcePak.getSystem().getClientSetting();
        FastCallProperties.Client globalSetting = properties.getClient();

        int connectTimeout = new PriorityValue<Integer>()
                .next(apiSetting.getConnectTimeout())
                .next(systemSetting.getConnectTimeout())
                .next(globalSetting.getConnectTimeout())
                .getValue();

        int readTimeout = new PriorityValue<Integer>()
                .next(apiSetting.getReadTimeout())
                .next(systemSetting.getReadTimeout())
                .next(globalSetting.getReadTimeout())
                .getValue();

        int writeTimeout = new PriorityValue<Integer>()
                .next(apiSetting.getWriteTimeout())
                .next(systemSetting.getWriteTimeout())
                .next(globalSetting.getWriteTimeout())
                .getValue();

        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .addInterceptor(authInceptor)
                .build();
    }

    public FastCallClient getClient(String systemCode, String apiName) {
        FcSourcePak sourcePak = source.getSourcePak(systemCode, apiName);
        FcSystemPak system = sourcePak.getSystem();
        if (!system.isEnable()) {
            throw new FcBizException(String.format("系统[%s]未配置启用", systemCode));
        }

        FcApiPak apiPak = sourcePak.getApi();
        if (Objects.isNull(apiPak)) {
            throw new FcDataNotFoundException(String.format("API[%s]未在系统[%s]中注册", apiName, systemCode));
        }

        String clientKey = FcUtils.calculateClientKey(system, apiPak);
        return CLIENT_MAP.computeIfAbsent(clientKey, k -> createNewCallClient(sourcePak))
                .withApi(apiPak);
    }

    public static FastCallClient getSystemClient(String systemCode) {
        return CLIENT_MAP.get("system");
    }
}
