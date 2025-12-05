package org.first.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 通用工具bean的定义
 * */
@Configuration
public class CommonBeanConfig {

    /**
     * 默认线程池
     * */
    @Bean(name = "defExecutor")
    public ThreadPoolTaskExecutor defExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(4);
        exec.setMaxPoolSize(8);
        exec.setQueueCapacity(200);
        exec.setThreadNamePrefix("def-exec-");
        exec.initialize();

        // 关键：配置拒绝策略（调用者线程执行，避免任务丢失，适合低并发兜底）
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return exec;
    }

}
