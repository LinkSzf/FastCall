package priv.szf.fastcall.core.config;


import lombok.AllArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@AllArgsConstructor
public class FcClientPoolConfig {

    private final FastCallProperties properties;

    @Bean
    public ConnectionPool clientConnectionPool() {
        return new ConnectionPool(
                properties.getPool().getMaxIdleConnections(),
                properties.getPool().getKeepAliveMinutes(),
                TimeUnit.MINUTES
        );
    }

    @Bean
    public ExecutorService clientThreadPool() {
        int corePoolSize = properties.getMaxRequestsPerHost() + 1;
        int maximumPoolSize = properties.getMaxRequests() + 1;
        int  keepAliveTime = 60;

        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    Thread thread = new Thread(r, "FastCall-Client-Thread");
                    thread.setDaemon(false);
                    return thread;
                },
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    @Bean
    public Dispatcher dispatcher() {
        Dispatcher dispatcher = new Dispatcher(clientThreadPool());
        dispatcher.setMaxRequests(properties.getMaxRequests());
        dispatcher.setMaxRequestsPerHost(properties.getMaxRequestsPerHost());
        return dispatcher;
    }




}
