package org.first.order.config;

import org.first.order.api.fallback.OrderApiFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description 自动装配fallback，避免责任变成使用方的
 * @since 25/11/27
 * */
@Configuration
public class OrderFeignAutoConfiguration {

    // 假设 orderApiFallbackFactory 是 FeignClient 的 Fallback 实现类
    @Bean
    public OrderApiFallbackFactory orderApiFallbackFactory() {
        return new OrderApiFallbackFactory();
    }
}