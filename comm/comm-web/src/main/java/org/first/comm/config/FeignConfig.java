package org.first.comm.config;

import feign.codec.ErrorDecoder;
import org.first.comm.decoder.GlobalFeignErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 2025/11/28
 * @description 向spring容器注册配置项
 * @remark @Component 是不行的。
 * @remark Feign 的全局配置必须用 @Configuration + @Bean 注册到 Feign 的 ConfigurationContext，
 * @remark 否则不会被 FeignClient 识别和加载
 * */
@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder globalFeignErrorDecoder() {
        return new GlobalFeignErrorDecoder();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new GlobalFeignErrorDecoder();
    }
}


