package priv.szf.fastcall.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import priv.szf.fastcall.common.FastCallConsts;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync(proxyTargetClass = true)
@Configuration
public class FcAsyncConfig {

    public static final String EVENT_EXECUTOR = FastCallConsts.NAME + "-event-executor";

    private static final String EVENT_EXECUTOR_THREAD_NAME_PREFIX = FastCallConsts.NAME + "-event-";
    private static final int EVENT_EXECUTOR_QUEUE_CAPACITY = 10000;

    @ConditionalOnMissingBean(name = EVENT_EXECUTOR)
    @Bean(EVENT_EXECUTOR)
    public Executor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(EVENT_EXECUTOR_QUEUE_CAPACITY);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix(EVENT_EXECUTOR_THREAD_NAME_PREFIX);
        executor.initialize();
        log.info("{} default event executor initialized.", FastCallConsts.NAME);
        return executor;
    }

}

