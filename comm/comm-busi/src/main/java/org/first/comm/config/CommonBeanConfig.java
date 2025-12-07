package org.first.comm.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description 通用工具bean的定义
 * @since 2025/12/05
 * @author xr
 * @remark resources/META-INF/spring.factories配置了自动装配此类
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

    //后面改pom为【redisson-spring-boot-starter】就可以删去这个bean配置；
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                //.setPassword("xxx") // 可选
        ;
        return Redisson.create(config);
    }

}
