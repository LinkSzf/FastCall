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

    private static final String THREAD_NAME_PREFIX = FastCallConsts.NAME + "-async-";
    private static final int QUEUE_CAPACITY = 10000;

    @ConditionalOnMissingBean(name = FastCallConsts.ASYNC_EXECUTOR)
    @Bean({FastCallConsts.ASYNC_EXECUTOR})
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        log.info("{} default event executor initialized.", FastCallConsts.NAME);
        return executor;
    }

}

