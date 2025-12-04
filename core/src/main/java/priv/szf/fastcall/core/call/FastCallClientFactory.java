package priv.szf.fastcall.core.call;


import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.core.call.source.IFcSource;
import priv.szf.fastcall.core.call.source.SourcePak;
import priv.szf.fastcall.core.config.FastCallProperties;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class FastCallClientFactory {

    private final IFcSource source;

    private final FastCallProperties properties;

    private final ConnectionPool connectionPool;

    private final Dispatcher dispatcher;

    public FastCallClient createNewCallClient(String systemCode) {
        SourcePak sourcePak = source.getSourcePak(systemCode);
        OkHttpClient okHttpClient = initOkHttpClient(sourcePak);
        return new FastCallClient(okHttpClient);
    }

    private OkHttpClient initOkHttpClient(SourcePak sourcePak) {
        int connectTimeout = new PriorityValue<Integer>()
                .next(sourcePak.getSystem().getConnectTimeout())
                .next(properties.getClient().getConnectTimeout())
                .getValue();

        int readTimeout = new PriorityValue<Integer>()
                .next(sourcePak.getSystem().getReadTimeout())
                .next(properties.getClient().getReadTimeout())
                .getValue();

        int writeTimeout = new PriorityValue<Integer>()
                .next(sourcePak.getSystem().getWriteTimeout())
                .next(properties.getClient().getWriteTimeout())
                .getValue();

        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .connectionPool(connectionPool)
                .dispatcher(dispatcher)
                .build();
    }






}
