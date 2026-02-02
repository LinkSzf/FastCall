package priv.szf.fastcall.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import priv.szf.fastcall.common.FastCallConts;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync(proxyTargetClass = true)
@Configuration
public class FcAsyncConfig {

    public static final String EVENT_EXECUTOR = FastCallConts.NAME + "-event-executor";

    private static final String EVENT_EXECUTOR_THREAD_NAME_PREFIX = FastCallConts.NAME + "-event-";

    @ConditionalOnMissingBean(name = EVENT_EXECUTOR)
    @Bean(EVENT_EXECUTOR)
    public Executor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.setThreadNamePrefix(EVENT_EXECUTOR_THREAD_NAME_PREFIX);
        executor.initialize();
        log.info("{} default event executor initialized.", FastCallConts.NAME);
        return executor;
    }

}
