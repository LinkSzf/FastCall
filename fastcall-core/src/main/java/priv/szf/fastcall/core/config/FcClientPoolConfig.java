package priv.szf.fastcall.core.config;


import lombok.AllArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import priv.szf.fastcall.common.FastCallConsts;

import java.util.concurrent.*;

@Configuration
@AllArgsConstructor
public class FcClientPoolConfig {

    private static final String CLIENT_CONNECTION_POOL_NAME = FastCallConsts.NAME + "ClientConnectionPool";

    private static final String CLIENT_THREAD_POOL_NAME = FastCallConsts.NAME + "ClientThreadPool";

    private static final String DISPATCHER_NAME = FastCallConsts.NAME + "Dispatcher";

    private static final String CLIENT_THREAD_POOL_THREAD_NAME = FastCallConsts.NAME + "-Client-Thread";

    private final FastCallProperties properties;

    @Bean(CLIENT_CONNECTION_POOL_NAME)
    public ConnectionPool clientConnectionPool() {
        return new ConnectionPool(
                properties.getPool().getMaxIdleConnections(),
                properties.getPool().getKeepAliveMinutes(),
                TimeUnit.MINUTES
        );
    }

    @Bean(CLIENT_THREAD_POOL_NAME)
    public ExecutorService clientThreadPool() {
        int corePoolSize = properties.getMaxRequestsPerHost() + 1;
        int maximumPoolSize = properties.getMaxRequests() + 1;
        int keepAliveTime = 60;

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    Thread thread = new Thread(r, CLIENT_THREAD_POOL_THREAD_NAME);
                    thread.setDaemon(false);
                    return thread;
                },
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    @Bean(DISPATCHER_NAME)
    public Dispatcher dispatcher() {
        Dispatcher dispatcher = new Dispatcher(clientThreadPool());
        dispatcher.setMaxRequests(properties.getMaxRequests());
        dispatcher.setMaxRequestsPerHost(properties.getMaxRequestsPerHost());
        return dispatcher;
    }




}

